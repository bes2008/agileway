package com.jn.agileway.web.rest;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.util.List;

/***
 * 对标 ControllerAdvice
 */
public class GlobalRestResponseBodyHandlerConfiguration {
    private List<String> basePackages = Collects.newArrayList();
    private List<String> excludedBasePackages = Collects.newArrayList();
    private List<Class> assignableTypes = Collects.newArrayList();
    private List<Class> excludedAssignableTypes = Collects.newArrayList();
    private List<Class> annotations = Collects.newArrayList();
    private List<Class> excludedAnnotations = Collects.newArrayList();

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        if (basePackages != null) {
            this.basePackages.addAll(basePackages);
        }
    }

    public void addBasePackage(String basePackage){
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

    public void addExcludedBasePackage(String excludedBasePackage ){
        this.excludedBasePackages.add(excludedBasePackage);
    }

    public List<Class> getAssignableTypes() {
        return assignableTypes;
    }

    public void setAssignableTypes(List<Class> assignableTypes) {
        if (assignableTypes != null) {
            this.assignableTypes.addAll(assignableTypes);
        }
    }

    public void addAssignableType(Class clazz){
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

    public void addExcludedAssignableType(Class clazz){
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

    public void addAnnotation(Class annotation){
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

    public void addExcludedAnnotation(Class annotation){
        this.excludedAnnotations.add(annotation);
    }

    public boolean isAcceptable(final Class clazz) {
        return isAssignableTypeMatched(clazz) || isPackageMatched(Reflects.getPackageName(clazz)) || isAnnotationMatched(clazz);
    }

    private boolean isAnnotationMatched(final Class clazz) {
        if (Collects.anyMatch(annotations, new Predicate<Class>() {
            @Override
            public boolean test(Class annotationClass) {
                return Reflects.isAnnotationPresent(clazz, annotationClass);
            }
        })) {
            return Collects.noneMatch(annotations, new Predicate<Class>() {
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

    private boolean isPackageMatched(final String packageName) {
        if (Collects.anyMatch(basePackages, new Predicate<String>() {
            @Override
            public boolean test(String basePackage) {
                return packageName.startsWith(basePackage);
            }
        })) {
            return Collects.noneMatch(excludedBasePackages, new Predicate<String>() {
                @Override
                public boolean test(String excludeBasePackage) {
                    return packageName.startsWith(excludeBasePackage);
                }
            });
        }
        return false;
    }


}
