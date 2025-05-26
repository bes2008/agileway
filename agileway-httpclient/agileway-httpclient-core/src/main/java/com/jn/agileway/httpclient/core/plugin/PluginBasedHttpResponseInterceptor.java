package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.interceptor.HttpResponseInterceptor;

public class PluginBasedHttpResponseInterceptor implements HttpResponseInterceptor {

    private HttpMessagePlugin plugin;

    public PluginBasedHttpResponseInterceptor(HttpMessagePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void intercept(HttpResponse response) {
        if (!plugin.getResponseInterceptors().isEmpty() && plugin.availableFor(response)) {
            for (HttpResponseInterceptor interceptor : plugin.getResponseInterceptors()) {
                interceptor.intercept(response);
            }
        }
    }
}
