package com.jn.agileway.http.rest;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以从3个维度进行配置： package, class, annotation
 */
public class GlobalRestResponseBodyHandlerProperties {

    private List<String> basePackages;
    private List<String> excludedBasePackages;
    private List<String> excludedBasePackageClasses;

    private List<String> assignableTypes;
    private List<String> excludedAssignableTypes;

    private List<String> annotations;
    private List<String> excludedAnnotations;

    private List<String> excludedMethods;

    /**
     * 响应 体要忽略的字段，字段 只对标 @see com.jn.langx.http.rest.RestRespBody
     */
    private List<String> ignoredFields = Collects.newArrayList(Collects.newHashSet(
            GlobalRestHandlers.GLOBAL_IGNORED_REST_FIELDS
    ));

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }


    public void addBasePackage(String packageName) {
        if (Strings.isNotBlank(packageName)) {
            if (this.basePackages == null) {
                this.basePackages = new ArrayList<String>();
            }
            this.basePackages.add(packageName);
        }
    }

    public List<String> getExcludedBasePackages() {
        return excludedBasePackages;
    }

    public void setExcludedBasePackages(List<String> excludedBasePackages) {
        this.excludedBasePackages = excludedBasePackages;
    }

    public void addExcludedBasePackage(String packageName) {
        if (Strings.isNotBlank(packageName)) {
            if (this.excludedBasePackages == null) {
                this.excludedBasePackages = new ArrayList<String>();
            }
            this.excludedBasePackages.add(packageName);
        }
    }

    public List<String> getAssignableTypes() {
        return assignableTypes;
    }

    public void setAssignableTypes(List<String> assignableTypes) {
        this.assignableTypes = assignableTypes;
    }

    public void addAssignableType(String assignableType) {
        if (Strings.isNotBlank(assignableType)) {
            if (this.assignableTypes == null) {
                this.assignableTypes = new ArrayList<String>();
            }
            this.assignableTypes.add(assignableType);
        }
    }

    public List<String> getExcludedAssignableTypes() {
        return excludedAssignableTypes;
    }

    public void setExcludedAssignableTypes(List<String> excludedAssignableTypes) {
        this.excludedAssignableTypes = excludedAssignableTypes;
    }

    public void addExcludedAssignableType(String type) {
        if (Strings.isNotBlank(type)) {
            if (this.excludedAssignableTypes == null) {
                this.excludedAssignableTypes = new ArrayList<String>();
            }
            this.excludedAssignableTypes.add(type);
        }
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotation(String annotation) {
        if (Strings.isNotBlank(annotation)) {
            if (this.annotations == null) {
                this.annotations = new ArrayList<String>();
            }
            this.annotations.add(annotation);
        }
    }

    public List<String> getExcludedAnnotations() {
        return excludedAnnotations;
    }

    public void setExcludedAnnotations(List<String> excludedAnnotations) {
        this.excludedAnnotations = excludedAnnotations;
    }

    public void addExcludedAnnotation(String annotation) {
        if (Strings.isNotBlank(annotation)) {
            if (this.excludedAnnotations == null) {
                this.excludedAnnotations = new ArrayList<String>();
            }
            this.excludedAnnotations.add(annotation);
        }
    }

    public List<String> getExcludedBasePackageClasses() {
        return excludedBasePackageClasses;
    }

    public void setExcludedBasePackageClasses(List<String> excludedBasePackageClasses) {
        this.excludedBasePackageClasses = excludedBasePackageClasses;
    }

    public void addExcludedBasePackageClass(String klass) {
        if (Strings.isNotBlank(klass)) {
            if (this.excludedBasePackageClasses == null) {
                this.excludedBasePackageClasses = new ArrayList<String>();
            }
            this.excludedBasePackageClasses.add(klass);
        }
    }

    public List<String> getExcludedMethods() {
        return excludedMethods;
    }

    public void setExcludedMethods(List<String> excludedMethods) {
        this.excludedMethods = excludedMethods;
    }

    public void addExcludedMethod(String method) {
        if (Strings.isNotBlank(method)) {
            if (this.excludedMethods == null) {
                this.excludedMethods = new ArrayList<String>();
            }
            this.excludedMethods.add(method);
        }
    }

    public List<String> getIgnoredFields() {
        return ignoredFields;
    }

    public void setIgnoredFields(List<String> ignoredFields) {
        this.ignoredFields = Pipeline.of(ignoredFields).distinct().clearNulls().asList();
    }

    public void addIgnoredFields(String ignoredField) {
        if (Strings.isNotBlank(ignoredField)) {
            if (this.ignoredFields == null) {
                this.ignoredFields = new ArrayList<>();
            }
            this.ignoredFields.add(ignoredField);
            setIgnoredFields(ignoredFields);
        }
    }
}
