package com.jn.agileway.audit.core.resource.parser.clazz;

import com.jn.agileway.audit.core.annotation.ResourceId;
import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.annotation.ResourceName;
import com.jn.agileway.audit.core.annotation.ResourceType;
import com.jn.agileway.audit.core.model.Resource;
import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.valuegetter.MemberValueGetter;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 只创建一个实例即可
 *
 * @param <T> the
 * @see DefaultEntityClassResourceSupplierParser
 * @deprecated
 */
@Singleton
@Deprecated
public class AnnotatedEntityResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static final Logger logger = Loggers.getLogger(AnnotatedEntityResourceSupplierParser.class);
    public static final AnnotatedEntityResourceSupplierParser DEFAULT_INSTANCE = new AnnotatedEntityResourceSupplierParser();

    private final MemberNameEntityResourceSupplierParser delegate = MemberNameEntityResourceSupplierParser.DEFAULT_INSTANCE;

    @Override
    public EntityResourceSupplier<T> parse(Class<T> entityClass) {

        Map<String, MemberValueGetter> getterMap = parseByResourceMappingAnnotation(entityClass);
        if (Emptys.isEmpty(getterMap)) {
            Map<String, MemberValueGetter> getterMap2 = parseByResourceFieldAnnotation(entityClass);
            getterMap.putAll(getterMap2);
        }
        if (Emptys.isEmpty(getterMap)) {
            return null;
        }

        final EntityResourceSupplier supplier = new EntityResourceSupplier(entityClass);
        supplier.register(getterMap);

        // 判断是否缺少属性
        List<String> deficientProperties = supplier.getDeficientProperties();
        if (deficientProperties.isEmpty()) {
            // 不缺少，直接返回
            return supplier;
        }
        // 基于成员进行解析
        try {
            final EntityResourceSupplier memberBasedEntityResourceSupplier = delegate.parse(entityClass);
            if (memberBasedEntityResourceSupplier != null) {
                // 进行合并
                Collects.forEach(deficientProperties, new Consumer<String>() {
                    @Override
                    public void accept(String property) {
                        MemberValueGetter valueGetter = (MemberValueGetter) memberBasedEntityResourceSupplier.getPropertyValueGetter(property);
                        if (valueGetter != null) {
                            supplier.register(property, valueGetter);
                        }
                    }
                });
            }
        } catch (Throwable ex) {
            logger.error("Error occur when parse class {}", Reflects.getFQNClassName(entityClass));
        }
        return supplier;
    }

    /**
     * 基于 {@link com.jn.agileway.audit.core.annotation.ResourceMapping} 进行解析
     *
     * @return
     */
    private Map<String, MemberValueGetter> parseByResourceMappingAnnotation(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();
        ResourceMapping resourceMapping = Reflects.getAnnotation(entityClass, ResourceMapping.class);
        if (resourceMapping != null) {
            String idField = resourceMapping.id();
            parsePropertyByFieldName(entityClass, idField, map, Resource.RESOURCE_ID);

            String nameField = resourceMapping.name();
            parsePropertyByFieldName(entityClass, nameField, map, Resource.RESOURCE_NAME);

            String typeField = resourceMapping.type();
            parsePropertyByFieldName(entityClass, typeField, map, Resource.RESOURCE_TYPE);
        }
        return map;
    }

    private Map<String, MemberValueGetter> parseByResourceFieldAnnotation(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();

        parsePropertyByAnnotation(entityClass, ResourceId.class, map, Resource.RESOURCE_ID);

        parsePropertyByAnnotation(entityClass, ResourceName.class, map, Resource.RESOURCE_NAME);

        parsePropertyByAnnotation(entityClass, ResourceType.class, map, Resource.RESOURCE_TYPE);

        return map;
    }


    private void parsePropertyByAnnotation(Class<T> entityClass, final Class<? extends Annotation> annotationClass, Map<String, MemberValueGetter> map, String resourceProperty) {
        Member member = null;
        member = Pipeline.of(entityClass.getDeclaredMethods()).findFirst(new Predicate<Method>() {
            @Override
            public boolean test(Method f) {
                return !Modifiers.isStatic(f) && Reflects.hasAnnotation(f, annotationClass);
            }
        });

        if (member == null) {
            member = Pipeline.of(entityClass.getDeclaredFields()).findFirst(new Predicate<Field>() {
                @Override
                public boolean test(Field f) {
                    return !Modifiers.isStatic(f) && Reflects.hasAnnotation(f, annotationClass);
                }
            });
        }

        if (member != null) {
            map.put(resourceProperty, new MemberValueGetter(member));
        } else {
            logger.info("Can't find the resource {} property or getter in the class: {} when parse it using {} ", resourceProperty, entityClass, annotationClass);
        }
    }

    /**
     * 从entityClass 类中解析 resourceProperty ，解析时使用的字段名称是 fieldName，解析完之后放入map
     */
    private void parsePropertyByFieldName(Class<T> entityClass, String fieldName, Map<String, MemberValueGetter> map, String resourceProperty) {
        Member member = null;
        try {
            member = Reflects.getGetter(entityClass, fieldName);
            if (member == null) {
                member = Reflects.getAnyField(entityClass, fieldName);
            }
        } catch (Throwable ex) {
            // ignore it
        }
        if (member != null) {
            map.put(resourceProperty, new MemberValueGetter(member));
        } else {
            logger.info("Can't find the resource {} property in the class: {} when parse it using @ResourceMapping ", resourceProperty, entityClass);
        }
    }


}
