package com.jn.agileway.web.rest;

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
}
