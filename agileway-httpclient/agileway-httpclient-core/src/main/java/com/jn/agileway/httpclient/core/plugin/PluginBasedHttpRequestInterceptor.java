package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.langx.plugin.PluginRegistry;

class PluginBasedHttpRequestInterceptor implements HttpRequestInterceptor {

    private PluginRegistry httpMessagePluginRegistry;

    PluginBasedHttpRequestInterceptor(PluginRegistry httpMessagePluginRegistry) {
        this.httpMessagePluginRegistry = httpMessagePluginRegistry;
    }

    @Override
    public void intercept(HttpRequest request) {

    }
}
