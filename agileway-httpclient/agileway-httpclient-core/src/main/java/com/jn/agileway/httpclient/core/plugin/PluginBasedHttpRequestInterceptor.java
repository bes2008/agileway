package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.util.function.Predicate;

public class PluginBasedHttpRequestInterceptor implements HttpRequestInterceptor {

    private PluginRegistry httpMessagePluginRegistry;

    public PluginBasedHttpRequestInterceptor(PluginRegistry httpMessagePluginRegistry) {
        this.httpMessagePluginRegistry = httpMessagePluginRegistry;
    }

    @Override
    public void intercept(final HttpRequest request) {
        HttpMessagePlugin plugin = httpMessagePluginRegistry.findOne(HttpMessagePlugin.class, new Predicate<HttpMessagePlugin>() {
            @Override
            public boolean test(HttpMessagePlugin plugin) {
                return plugin.availableFor(request) && !plugin.getRequestInterceptors().isEmpty();
            }
        });
        if (plugin != null) {
            for (HttpRequestInterceptor interceptor : plugin.getRequestInterceptors()) {
                interceptor.intercept(request);
            }
        }
    }
}
