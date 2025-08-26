package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;

public class PluginBasedHttpRequestInterceptor implements HttpRequestInterceptor {

    private HttpMessageProtocolPlugin plugin;

    public PluginBasedHttpRequestInterceptor(HttpMessageProtocolPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void intercept(final HttpRequest request) {
        if (!plugin.getRequestInterceptors().isEmpty() && plugin.availableFor(request)) {
            for (HttpRequestInterceptor interceptor : plugin.getRequestInterceptors()) {
                interceptor.intercept(request);
            }
        }
    }
}
