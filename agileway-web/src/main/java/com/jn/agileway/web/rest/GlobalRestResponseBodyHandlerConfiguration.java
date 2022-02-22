package com.jn.agileway.web.rest;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/***
 * 对标 ControllerAdvice
 */
public class GlobalRestResponseBodyHandlerConfiguration {
    /**
     * 该配置指定包下的类会被应用统一响应
     */
    private List<String> basePackages = Collects.newArrayList();
    /**
     * 该配置指定包下的类不会被应用统一响应
     */
    private List<String> excludedBasePackages = Collects.newArrayList();
    /**
     * 该配置指定的类不会被应用统一响应
     */
    private List<Class> excludedBasePackageClasses = Collects.newArrayList();

    /**
     * 该配置指定的类的类以及子类会被应用统一响应
     */
    private List<Class> assignableTypes = Collects.newArrayList();
    /**
     * 该配置指定的类的类以及子类不会会被应用统一响应
     */
    private List<Class> excludedAssignableTypes = Collects.newArrayList();
    /**
     * 被该配置指定的注解标注的类会被应用统一响应
     */
    private List<Class> annotations = Collects.newArrayList();
    /**
     * 被该配置指定的注解标注的类不会被应用统一响应
     */
    private List<Class> excludedAnnotations = Collects.newArrayList();

    private Set<String> excludedMethods = new CopyOnWriteArraySet<String>();

    /**
     * 响应 体要忽略的字段，字段 只对标 @see com.jn.langx.http.rest.RestRespBody
     *
     *
     * @see GlobalRestHandlers#GLOBAL_REST_FIELD_SUCCESS
     *
     */
    private Set<String> ignoredFields = Collects.newHashSet();

    public List<String> getBasePackages() {
        return basePackages;
    }


    public void setBasePackages(List<String> basePackages) {
        if (basePackages != null) {
            this.basePackages.addAll(basePackages);
        }
    }

    public void addBasePackage(String basePackage) {
        this.basePackages.add(basePackage);
    }

    public List<String> getExcludedBasePackages() {
        return excludedBasePackages;
    }

    public void setExcludedBasePackages(List<String> excludedBasePackages) {
        if (excludedBasePackages != null) {
            this.excludedBasePackages.addAll(excludedBasePackages);
        }
    }

    public void addExcludedBasePackage(String excludedBasePackage) {
        this.excludedBasePackages.add(excludedBasePackage);
    }

    public List<Class> getExcludedBasePackageClasses() {
        return excludedBasePackageClasses;
    }

    public void setExcludedBasePackageClasses(List<Class> excludedBasePackageClasses) {
        this.excludedBasePackageClasses = excludedBasePackageClasses;
    }

    public List<Class> getAssignableTypes() {
        return assignableTypes;
    }

    public void setAssignableTypes(List<Class> assignableTypes) {
        if (assignableTypes != null) {
            this.assignableTypes.addAll(assignableTypes);
        }
    }

    public void addAssignableType(Class clazz) {
        this.assignableTypes.add(clazz);
    }

    public List<Class> getExcludedAssignableTypes() {
        return excludedAssignableTypes;
    }

    public void setExcludedAssignableTypes(List<Class> excludedAssignableTypes) {
        if (excludedAssignableTypes != null) {
            this.excludedAssignableTypes.addAll(excludedAssignableTypes);
        }
    }

    public void addExcludedAssignableType(Class clazz) {
        this.excludedAssignableTypes.add(clazz);
    }

    public List<Class> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Class> annotations) {
        if (annotations != null) {
            this.annotations.addAll(annotations);
        }
    }

    public void addAnnotation(Class annotation) {
        this.annotations.add(annotation);
    }

    public List<Class> getExcludedAnnotations() {
        return excludedAnnotations;
    }

    public void setExcludedAnnotations(List<Class> excludedAnnotations) {
        if (excludedAnnotations != null) {
            this.excludedAnnotations.addAll(excludedAnnotations);
        }
    }

    public void addExcludedAnnotation(Class annotation) {
        this.excludedAnnotations.add(annotation);
    }

    public boolean isAcceptable(final Class clazz) {
        return isAssignableTypeMatched(clazz) || isPackageMatched(clazz) || isAnnotationMatched(clazz);
    }

    public boolean isAcceptable(final Method method) {
        // 有@RestAction注解时，根据该注解来判断
        String className = Reflects.getFQNClassName(method.getDeclaringClass());
        String methodFQN = className + "." + method.getName();
        if (Reflects.hasAnnotation(method, RestAction.class)) {
            RestAction restAction = Reflects.getAnnotation(method, RestAction.class);
            return restAction.value();
        }

        // 没有注解时，根据配置来判断
        if (isAcceptable(method.getDeclaringClass())) {
            if (isExcludedMethod(methodFQN)) {
                return false;
            }
            // 存在 excludedAnnotations 中的任何一个
            if (Collects.anyMatch(this.excludedAnnotations, new Predicate<Class>() {
                @Override
                public boolean test(Class excludedAnnotationClass) {
                    return Reflects.hasAnnotation(method, excludedAnnotationClass);
                }
            })) {
                return false;
            }
            return true;
        } else {
            // 存在 excludedAnnotations 中的任何一个
            if (Collects.anyMatch(this.annotations, new Predicate<Class>() {
                @Override
                public boolean test(Class annotationClass) {
                    return Reflects.hasAnnotation(method, annotationClass);
                }
            })) {
                return true;
            }
            return false;
        }
    }

    private boolean isAnnotationMatched(final Class clazz) {
        if (Collects.anyMatch(annotations, new Predicate<Class>() {
            @Override
            public boolean test(Class annotationClass) {
                return Reflects.isAnnotationPresent(clazz, annotationClass);
            }
        })) {
            return Collects.noneMatch(excludedAnnotations, new Predicate<Class>() {
                @Override
                public boolean test(Class annotationClass) {
                    return Reflects.isAnnotationPresent(clazz, annotationClass);
                }
            });
        }
        return false;
    }

    private boolean isAssignableTypeMatched(final Class clazz) {
        if (Collects.anyMatch(assignableTypes, new Predicate<Class>() {
            @Override
            public boolean test(Class assignableType) {
                return Reflects.isSubClassOrEquals(assignableType, clazz);
            }
        })) {
            return Collects.noneMatch(excludedAssignableTypes, new Predicate<Class>() {
                @Override
                public boolean test(Class assignableType) {
                    return Reflects.isSubClassOrEquals(assignableType, clazz);
                }
            });
        }
        return false;
    }

    private boolean isPackageMatched(final Class clazz) {

        final String packageName = clazz.getName();
        // 不在指定的包下
        if (Collects.noneMatch(basePackages, new Predicate<String>() {
            @Override
            public boolean test(String basePackage) {
                return packageName.startsWith(basePackage);
            }
        })) {
            return false;
        }

        // 在任何一个指定的排除包下，就是要排除的
        if (Collects.anyMatch(excludedBasePackages, new Predicate<String>() {
            @Override
            public boolean test(String excludeBasePackage) {
                return packageName.startsWith(excludeBasePackage);
            }
        })) {
            return false;
        }

        // 是任何一个要排除的类时
        if (Collects.anyMatch(excludedBasePackageClasses, new Predicate<Class>() {
            @Override
            public boolean test(Class value) {
                return Reflects.isSubClassOrEquals(clazz, value);
            }
        })) {
            return false;
        }
        return true;
    }

    public void addExcludedMethod(String methodFQN) {
        if (Emptys.isNotEmpty(methodFQN)) {
            this.excludedMethods.add(methodFQN);
        }
    }

    public void addExcludedMethod(Method method) {
        String className = Reflects.getFQNClassName(method.getDeclaringClass());
        addExcludedMethod(className + "." + method.getName());
    }

    public void addExcludedMethods(List<String> methodFQNs) {
        Collects.forEach(methodFQNs, new Consumer<String>() {
            @Override
            public void accept(String methodFQN) {
                addExcludedMethod(methodFQN);
            }
        });
    }

    /**
     * 是否为在要排除的方法
     *
     * @param method
     */
    public boolean isExcludedMethod(String method) {
        return this.excludedMethods.contains(method);
    }

    public Set<String> getIgnoredFields() {
        return ignoredFields;
    }

    public void setIgnoredFields(Set<String> ignoredFields) {
        if(ignoredFields!=null) {
            this.ignoredFields = ignoredFields;
        }
    }

    public boolean isIgnoredField(String fieldName){
        if(Strings.isBlank(fieldName)){
            return true;
        }
        return ignoredFields.contains(fieldName);
    }
}
