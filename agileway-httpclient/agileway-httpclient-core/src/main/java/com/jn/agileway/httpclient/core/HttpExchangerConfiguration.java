package com.jn.agileway.httpclient.core;

import java.util.List;

public class HttpExchangerConfiguration {
    private List<String> allowedSchemes;
    private List<String> allowedAuthorities;
    private List<String> notAllowedAuthorities;
    private List<String> allowedMethods;
    private List<String> notAllowedMethods;

    public List<String> getAllowedSchemes() {
        return allowedSchemes;
    }

    public void setAllowedSchemes(List<String> allowedSchemes) {
        this.allowedSchemes = allowedSchemes;
    }

    public List<String> getAllowedAuthorities() {
        return allowedAuthorities;
    }

    public void setAllowedAuthorities(List<String> allowedAuthorities) {
        this.allowedAuthorities = allowedAuthorities;
    }

    public List<String> getNotAllowedAuthorities() {
        return notAllowedAuthorities;
    }

    public void setNotAllowedAuthorities(List<String> notAllowedAuthorities) {
        this.notAllowedAuthorities = notAllowedAuthorities;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getNotAllowedMethods() {
        return notAllowedMethods;
    }

    public void setNotAllowedMethods(List<String> notAllowedMethods) {
        this.notAllowedMethods = notAllowedMethods;
    }
}
