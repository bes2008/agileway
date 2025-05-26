package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.interceptor.HttpResponseInterceptor;
import com.jn.langx.plugin.PluginRegistry;

public class PluginBasedHttpResponseInterceptor implements HttpResponseInterceptor {

    private PluginRegistry httpMessagePluginRegistry;

    public PluginBasedHttpResponseInterceptor(PluginRegistry pluginRegistry) {
        this.httpMessagePluginRegistry = pluginRegistry;
    }

    @Override
    public void intercept(HttpResponse response) {

    }
}
