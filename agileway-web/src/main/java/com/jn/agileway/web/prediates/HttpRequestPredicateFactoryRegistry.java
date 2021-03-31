package com.jn.agileway.web.prediates;

import com.jn.langx.registry.Registry;

import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestPredicateFactoryRegistry implements Registry<String, HttpRequestPredicateFactory> {
    private ConcurrentHashMap<String, HttpRequestPredicateFactory> factories = new ConcurrentHashMap<String, HttpRequestPredicateFactory>();

    @Override
    public void register(HttpRequestPredicateFactory factory) {
        register(factory.getName(), factory);
    }

    @Override
    public void register(String key, HttpRequestPredicateFactory factory) {
        if (factory != null) {
            factories.put(key, factory);
        }
    }

    @Override
    public HttpRequestPredicateFactory get(String name) {
        return factories.get(name);
    }

}
