package com.jn.agileway.audit.core.resource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.annotation.ResourceId;
import com.jn.agileway.audit.core.model.*;
import com.jn.agileway.audit.core.resource.idresource.AbstractIdResourceExtractor;
import com.jn.agileway.audit.core.resource.parser.clazz.CustomNamedEntityResourceSupplierParser;
import com.jn.agileway.audit.core.resource.parser.parameter.*;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.ConcurrentReferenceHashMap;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.parameter.MethodParameter;
import com.jn.langx.util.reflect.reference.ReferenceType;
import com.jn.langx.util.reflect.type.Types;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.valuegetter.ArrayValueGetter;
import com.jn.langx.util.valuegetter.PipelineValueGetter;
import com.jn.langx.util.valuegetter.StreamValueGetter;
import com.jn.langx.util.valuegetter.ValueGetter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提供了基于正在执行的方法的ResourceExtractor
 * 必须在 operation extractor 之后执行才有意义
 *
 * @param <AuditedRequest>
 * @since audit 1.0.3+, jdk 1.7+
 */
public class ResourceMethodInvocationExtractor<AuditedRequest> implements ResourceExtractor<AuditedRequest, MethodInvocation> {
    /**
     * 根据注解解析后的进行缓存
     * key: 在执行的方法
     * value: 基于方法参数的 ValueGetter
     */
    private ConcurrentReferenceHashMap<Method, Holder<ValueGetter>> annotatedCache = new ConcurrentReferenceHashMap<Method, Holder<ValueGetter>>(1000, 0.9f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.SOFT);
    /**
     * 根据配置文件定义解析之后的缓存
     * <p>
     * key: 在执行的方法
     * value: 基于方法参数的 ValueGetter
     */
    private ConcurrentReferenceHashMap<Method, Entry<ResourceDefinition, ValueGetter>> configuredResourceCache = new ConcurrentReferenceHashMap<Method, Entry<ResourceDefinition, ValueGetter>>(1000, 0.9f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.SOFT);

    private AbstractIdResourceExtractor idResourceExtractor;


    @Override
    public List<Resource> get(AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        final MethodInvocation methodInvocation = wrappedRequest.getRequestContext();
        Method method = methodInvocation.getJoinPoint();

        AuditEvent auditEvent = wrappedRequest.getAuditEvent();

        Operation operation = auditEvent.getOperation();
        if (operation == null) {
            return null;
        }
        OperationDefinition operationDefinition = operation.getDefinition();
        if (operationDefinition == null) {
            return null;
        }

        if (Emptys.isNotEmpty(auditEvent.getResources())) {
            return auditEvent.getResources();
        }

        // step find supplier and extract resource

        // step 1：根据 method 从 cache 里找到对应的 supplier
        ResourceDefinition resourceDefinition = operationDefinition.getResourceDefinition();
        ValueGetter resourceGetter = getValueGetterFromCache(method, resourceDefinition);
        if (resourceGetter == null) {

            Parameter[] parameters = Collects.toArray(Reflects.getMethodParameters("langx_aspectj", method), MethodParameter[].class);

            // step 2：如果 step 1 没找到，根据 resource definition 去解析 生成 supplier
            // step 2.1 : 根据注解
            if (resourceDefinition.isAnnotationEnabled() && resourceDefinition.isAnnotationFirst()) {
                if (!annotatedCache.containsKey(method)) {
                    resourceGetter = parseResourceGetterByAnnotation(parameters);
                    annotatedCache.putIfAbsent(method, new Holder<ValueGetter>(resourceGetter));
                }
            }

            if (resourceGetter == null) {
                // step 2.2：如果 step 2.1 没找到，根据 resource definition 去解析 生成 supplier
                resourceGetter = parseResourceGetterByConfiguration(parameters, resourceDefinition);
                if (resourceGetter != null) {
                    configuredResourceCache.put(method, new Entry<ResourceDefinition, ValueGetter>(resourceDefinition, resourceGetter));
                }
            }

            if (resourceGetter == null) {
                if (resourceDefinition.isAnnotationEnabled() && !resourceDefinition.isAnnotationFirst()) {
                    // step 3: 如果 step 2 没找到，根据 注解去解析 生成 supplier
                    if (!annotatedCache.containsKey(method)) {
                        resourceGetter = parseResourceGetterByAnnotation(parameters);
                        annotatedCache.putIfAbsent(method, new Holder<ValueGetter>(resourceGetter));
                    }
                }
            }
        }
        // step 4: 如果 step 3 没找到， null
        if (resourceGetter == null) {
            return null;
        }
        List<Resource> resources = Collects.asList(Collects.<Resource>asIterable(resourceGetter.get(methodInvocation.getArguments())));

        // 通常应该在 数据访问层执行下面的代码，例如mybatis 通用的 service 层
        if (idResourceExtractor != null) {
            if (Collects.allMatch(resources, new Predicate<Resource>() {
                @Override
                public boolean test(Resource value) {
                    return ResourceUtils.isOnlyResourceId(value);
                }
            })) {
                List<Serializable> ids = Pipeline.of(resources).map(new Function<Resource, Serializable>() {
                    @Override
                    public Serializable apply(Resource resource) {
                        return resource.getResourceId();
                    }
                }).asList();

                // 通常应该在 数据访问层执行下面的代码，例如mybatis 通用的 service 层
                List<Resource> resourceList = idResourceExtractor.findResources(wrappedRequest, ids);
                resourceList = Pipeline.of(resourceList).filter(new Predicate<Resource>() {
                    @Override
                    public boolean test(Resource resource) {
                        return ResourceUtils.isValid(resource);
                    }
                }).asList();
                return resourceList;
            }
        }
        return resources;
    }

    private ValueGetter getValueGetterFromCache(@NonNull Method method, @NonNull ResourceDefinition resourceDefinition) {
        boolean annotationEnabled = resourceDefinition.isAnnotationEnabled();
        boolean annotationFirst = resourceDefinition.isAnnotationFirst();
        ValueGetter valueGetter = null;
        if (annotationEnabled && annotationFirst) {
            Holder<ValueGetter> holder = annotatedCache.get(method);
            if (holder != null) {
                valueGetter = holder.get();
            }
        }
        if (valueGetter != null) {
            return valueGetter;
        }


        Entry<ResourceDefinition, ValueGetter> entry = configuredResourceCache.get(method);
        if (entry != null) {
            if (Objs.equals(entry.getKey(), resourceDefinition)) {
                valueGetter = entry.getValue();
            }
        }
        if (valueGetter != null) {
            return valueGetter;
        }
        if (annotationEnabled && !annotationFirst) {
            Holder<ValueGetter> holder = annotatedCache.get(method);
            if (holder != null) {
                valueGetter = holder.get();
            }
        }
        return valueGetter;
    }

    private ValueGetter parseResourceGetterByConfiguration(Parameter[] parameters, ResourceDefinition resourceDefinition) {
        ValueGetter resourceGetter = null;

        if (resourceDefinition != null) {
            Map<String, Parameter> parameterMap = Pipeline.of(parameters)
                    .collect(Collects.toHashMap(
                            new Function<Parameter, String>() {
                                @Override
                                public String apply(Parameter parameter) {
                                    return parameter.getName();
                                }
                            }, new Function<Parameter, Parameter>() {
                                @Override
                                public Parameter apply(Parameter parameter) {
                                    return parameter;
                                }
                            }, true));

            Map<String, Object> mapping = resourceDefinition;
            // step 1 : parse key: resource
            String resourceKey = resourceDefinition.getResource();
            if (Emptys.isNotEmpty(resourceKey)) {
                Parameter parameter = parameterMap.get(resourceKey);

                ResourceSupplier supplier = null;
                if (parameter != null) {
                    Class parameterType = parameter.getType();
                    Class parameterType0 = parameterType;
                    if (Types.isArray(parameterType)) {
                        parameterType0 = parameterType.getComponentType();
                    } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                        try {
                            Type parameterTypeXX = parameter.getParameterizedType();
                            parameterType0 = getUniqueGenericArgumentType(parameterTypeXX);
                            if (parameterType0 == null) {
                                parameterType0 = parameterType;
                            }
                        } catch (Throwable ex) {
                            parameterType0 = parameterType;
                        }
                    }

                    if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                        supplier = new CustomNamedMapParameterResourceSupplierParser(mapping).parse(parameter);
                    } else if (!Types.isLiteralType(parameterType0)) {
                        //if (parameterType0 != parameterType) {
                            // @Resource Array, @Resource Collection
                            // @Resource javaBean
                        //    supplier = new CustomNamedEntityResourceSupplierParser(mapping).parse(parameterType0);
                        //}else{
                            supplier = new CustomNamedEntityResourceSupplierParser(mapping).parse(parameterType0);
                        //}
                    }

                    if (supplier != null) {
                        int index = Collects.firstOccurrence(Collects.asList(parameters), parameter);
                        PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                        pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));
                        if (parameterType0 == parameterType) {
                            // java bean
                            pipelineValueGetter.addValueGetter(supplier);
                        } else {
                            // array, collection
                            pipelineValueGetter.addValueGetter(new StreamValueGetter(supplier));
                        }
                        resourceGetter = pipelineValueGetter;
                    }
                }
            }

            // step 2: parse by resourceId, resourceName, resourceType
            // 这一步只针对字面量类型的解析
            if (resourceGetter == null) {
                // 这个解析，不要求 resourceId, resourceName, resourceType 这三个都必须有
                resourceGetter = new CustomResourcePropertyParameterResourceSupplierParser(mapping).parse(parameters);
            }

            // step 3: 解析 Collection ids
            // 这一步只针对 id 是Collection或者Array
            if (resourceGetter == null && Emptys.isNotEmpty(resourceDefinition.getResourceId())) {
                MethodParameter parameter = (MethodParameter) parameterMap.get(resourceDefinition.getResourceId());
                if (parameter != null) {
                    Class parameterType = parameter.getType();
                    Class parameterType0 = parameterType;
                    boolean isArray = false;
                    boolean isCollection = false;
                    if (Types.isArray(parameterType)) {
                        parameterType0 = parameterType.getComponentType();
                        isArray = true;
                    } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                        try {
                            Type parameterTypeXX = parameter.getParameterizedType();
                            parameterType0 = getUniqueGenericArgumentType(parameterTypeXX);
                            if (parameterType0 == null) {
                                parameterType0 = parameterType;
                            }
                            isCollection = true;
                        } catch (Throwable ex) {
                            parameterType0 = parameterType;
                        }
                    }

                    if (parameterType0 != null && Types.isLiteralType(parameterType0)) {
                        if (isArray || isCollection) {
                            PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                            // 相当于调用 parameters[index]
                            List parameterList = Collects.asList(parameters);
                            int index = Collects.firstOccurrence(parameterList, parameter);
                            pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));

                            pipelineValueGetter.addValueGetter(new StreamValueGetter(new Function() {
                                        @Override
                                        public Object apply(Object id) {
                                            Resource resource = new Resource();
                                            if (id == null) {
                                                return null;
                                            }
                                            resource.setResourceId(id.toString());
                                            return resource;
                                        }
                                    })
                            );

                            resourceGetter = pipelineValueGetter;
                        }
                    }
                }
            }

        }
        return resourceGetter;
    }

    private ValueGetter parseResourceGetterByAnnotation(final Parameter[] parameters) {
        final Holder<ValueGetter> resourceGetter = new Holder<ValueGetter>();
        // step 1: 解析 @Resource 注解
        Collects.forEach(parameters,
                new Predicate2<Integer, Parameter>() {
                    @Override
                    public boolean test(Integer key, Parameter parameter) {
                        return Reflects.hasAnnotation(parameter, com.jn.agileway.audit.core.annotation.Resource.class);
                    }
                },
                new Consumer2<Integer, Parameter>() {
                    @Override
                    public void accept(Integer index, Parameter parameter) {
                        ValueGetter supplier = null;
                        Class parameterType = parameter.getType();
                        Class parameterType0 = parameterType;
                        boolean isArray = false;
                        boolean isCollection = false;
                        if (Types.isArray(parameterType)) {
                            parameterType0 = parameterType.getComponentType();
                            isArray = true;
                        } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                            try {
                                Type parameterTypeXX = parameter.getParameterizedType();
                                parameterType0 = getUniqueGenericArgumentType(parameterTypeXX);
                                if (parameterType0 == null) {
                                    parameterType0 = parameterType;
                                }
                                isCollection = true;
                            } catch (Throwable ex) {
                                parameterType0 = parameterType;
                            }
                        }

                        // 对泛型 argument type进行处理
                        // 也就是这里是支持： Collection<Map>, Collection<Entity>, Map, Entity的方式，不对 Collection<Collection>的方式做支持
                        if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                            supplier = new ResourceAnnotatedMapParameterResourceSupplierParser().parse(parameter);
                        } else if (!Types.isLiteralType(parameterType0)) {
                            if (parameterType0 != parameterType) {
                                supplier = new ResourceAnnotatedCollectionParameterResourcesSupplierParser(parameterType0).parse(parameter);
                            } else {
                                supplier = new ResourceAnnotatedEntityParameterResourceSupplierParser().parse(parameter);
                            }
                        }

                        if (supplier != null) {
                            PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                            // 相当于调用 parameters[index]
                            pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));

                            if (!isArray && !isCollection) {
                                // 此时为 Map 或者 Entity
                                pipelineValueGetter.addValueGetter(supplier);
                            } else {
                                // 此时 为 array, collection
                                pipelineValueGetter.addValueGetter(new StreamValueGetter(supplier));
                            }
                            resourceGetter.set(pipelineValueGetter);
                        }
                    }
                },
                new Predicate2<Integer, Parameter>() {
                    @Override
                    public boolean test(Integer key, Parameter value) {
                        return !resourceGetter.isNull();
                    }
                }
        );

        // step 2: 解析 @ResourceId, @ResourceName, @ResourceType 注解
        // 这一步只针对字面量类型的解析
        if (resourceGetter.isNull()) {
            // 这个解析，不要求  @ResourceId, @ResourceName, @ResourceType 这三个都必须有
            resourceGetter.set(new ResourcePropertyAnnotatedResourceSupplierParser().parse(parameters));
        }
        // step 3: 解析 Collection ids
        // 这一步只针对 id 是Collection或者Array， 并且有 @ResourceId 标注的情况
        if (resourceGetter.isNull()) {

            Collects.forEach(parameters,
                    new Predicate2<Integer, Parameter>() {
                        @Override
                        public boolean test(Integer key, Parameter parameter) {
                            return Reflects.hasAnnotation(parameter, ResourceId.class);
                        }
                    },
                    new Consumer2<Integer, Parameter>() {
                        @Override
                        public void accept(Integer index, Parameter parameter) {
                            Class parameterType = parameter.getType();
                            Class parameterType0 = parameterType;
                            boolean isArray = false;
                            boolean isCollection = false;
                            if (Types.isArray(parameterType)) {
                                parameterType0 = parameterType.getComponentType();
                                isArray = true;
                            } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                                try {
                                    Type parameterTypeXX = parameter.getParameterizedType();
                                    parameterType0 = getUniqueGenericArgumentType(parameterTypeXX);
                                    if (parameterType0 == null) {
                                        parameterType0 = parameterType;
                                    }
                                    isCollection = true;
                                } catch (Throwable ex) {
                                    parameterType0 = parameterType;
                                }
                            }

                            if (parameterType0 != null && Types.isLiteralType(parameterType0)) {

                                if (isArray || isCollection) {
                                    PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                                    // 相当于调用 parameters[index]
                                    pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));

                                    pipelineValueGetter.addValueGetter(new StreamValueGetter(new Function() {
                                                @Override
                                                public Object apply(Object id) {
                                                    Resource resource = new Resource();
                                                    if (id == null) {
                                                        return null;
                                                    }
                                                    resource.setResourceId(id.toString());
                                                    return resource;
                                                }
                                            })
                                    );

                                    resourceGetter.set(pipelineValueGetter);
                                }
                            }
                        }
                    },
                    new Predicate2<Integer, Parameter>() {
                        @Override
                        public boolean test(Integer key, Parameter value) {
                            return !resourceGetter.isNull();
                        }
                    }
            );
        }
        return resourceGetter.get();
    }


    public AbstractIdResourceExtractor getIdResourceExtractor() {
        return idResourceExtractor;
    }

    public void setIdResourceExtractor(AbstractIdResourceExtractor idResourceExtractor) {
        this.idResourceExtractor = idResourceExtractor;
    }

    public static Class getUniqueGenericArgumentType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] argumentTypes = parameterizedType.getActualTypeArguments();
            if (Objs.isNotEmpty(argumentTypes) && Objs.length(argumentTypes) == 1) {
                Type argumentType = argumentTypes[0];
                if (Types.isClass(argumentType)) {
                    return Types.toClass(argumentType);
                }
            }
        }
        return null;
    }
}
