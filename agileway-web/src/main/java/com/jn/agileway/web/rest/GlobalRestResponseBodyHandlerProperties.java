package com.jn.agileway.web.rest;

import java.util.List;

public class GlobalRestResponseBodyHandlerProperties {
    private List<String> basePackages;
    private List<String> excludedBasePackages;
    private List<String> assignableTypes;
    private List<String> excludedAssignableTypes;
    private List<String> annotations;
    private List<String> excludedAnnotations;

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
}
