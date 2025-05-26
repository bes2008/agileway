package com.jn.agileway.httpclient.core.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.interceptor.HttpResponseInterceptor;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.util.function.Predicate;

public class PluginBasedHttpResponseInterceptor implements HttpResponseInterceptor {

    private PluginRegistry httpMessagePluginRegistry;

    public PluginBasedHttpResponseInterceptor(PluginRegistry pluginRegistry) {
        this.httpMessagePluginRegistry = pluginRegistry;
    }

    @Override
    public void intercept(HttpResponse response) {
        HttpMessagePlugin plugin = httpMessagePluginRegistry.findOne(HttpMessagePlugin.class, new Predicate<HttpMessagePlugin>() {
            @Override
            public boolean test(HttpMessagePlugin plugin) {
                return plugin.availableFor(response) && !plugin.getResponseInterceptors().isEmpty();
            }
        });
        if (plugin != null) {
            for (HttpResponseInterceptor interceptor : plugin.getResponseInterceptors()) {
                interceptor.intercept(response);
            }
        }
    }
}
