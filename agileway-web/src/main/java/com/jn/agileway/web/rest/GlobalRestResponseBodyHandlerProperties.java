package com.jn.agileway.web.rest;

import com.jn.langx.util.collection.Collects;

import java.util.List;
import java.util.Set;

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
    private Set<String> ignoredFields = Collects.newHashSet(
            GlobalRestHandlers.GLOBAL_IGNORED_REST_FIELDS
    );

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public List<String> getExcludedBasePackages() {
        return excludedBasePackages;
    }

    public void setExcludedBasePackages(List<String> excludedBasePackages) {
        this.excludedBasePackages = excludedBasePackages;
    }

    public List<String> getAssignableTypes() {
        return assignableTypes;
    }

    public void setAssignableTypes(List<String> assignableTypes) {
        this.assignableTypes = assignableTypes;
    }

    public List<String> getExcludedAssignableTypes() {
        return excludedAssignableTypes;
    }

    public void setExcludedAssignableTypes(List<String> excludedAssignableTypes) {
        this.excludedAssignableTypes = excludedAssignableTypes;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getExcludedAnnotations() {
        return excludedAnnotations;
    }

    public void setExcludedAnnotations(List<String> excludedAnnotations) {
        this.excludedAnnotations = excludedAnnotations;
    }

    public List<String> getExcludedBasePackageClasses() {
        return excludedBasePackageClasses;
    }

    public void setExcludedBasePackageClasses(List<String> excludedBasePackageClasses) {
        this.excludedBasePackageClasses = excludedBasePackageClasses;
    }

    public List<String> getExcludedMethods() {
        return excludedMethods;
    }

    public void setExcludedMethods(List<String> excludedMethods) {
        this.excludedMethods = excludedMethods;
    }

    public Set<String> getIgnoredFields() {
        return ignoredFields;
    }

    public void setIgnoredFields(Set<String> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }
}
