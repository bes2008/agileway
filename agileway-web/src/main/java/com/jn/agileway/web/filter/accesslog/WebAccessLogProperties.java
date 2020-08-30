package com.jn.agileway.web.filter.accesslog;


import com.jn.langx.util.Objects;

import java.util.List;

/**
 * 不提供基于URL pattern的过滤方式，原因是注册 filter时，规范就要求指定 url pattern
 */
public class WebAccessLogProperties {
    private AccessLogLevel level = AccessLogLevel.BASIC;
    private boolean logResponse = true;
    private List<String> urlPatterns;

    public AccessLogLevel getLevel() {
        return Objects.useValueIfNull(level, AccessLogLevel.BASIC);
    }

    public void setLevel(AccessLogLevel level) {
        this.level = level;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }
}
