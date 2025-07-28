package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.langx.Builder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class DeclarativeHttpServiceProxyBuilder<S> implements Builder<S> {
    private Exchanger exchanger;
    private HttpExchangeMethodResolver methodResolver;
    private DeclarativeHttpRequestFactory requestFactory;

    private Class<S> serviceInterface;

    public DeclarativeHttpServiceProxyBuilder() {

    }

    public final DeclarativeHttpServiceProxyBuilder<S> withExchanger(Exchanger exchanger) {
        this.exchanger = exchanger;
        return this;
    }

    public final DeclarativeHttpServiceProxyBuilder<S> withMethodResolver(HttpExchangeMethodResolver methodResolver) {
        this.methodResolver = methodResolver;
        return this;
    }

    public final DeclarativeHttpServiceProxyBuilder<S> requestFactory(DeclarativeHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
        return this;
    }

    public final S build() {
        Map<Method, HttpExchangeMethod> httpExchangeMethods = resolveServiceMethods();
        return (S) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{serviceInterface}, new HttpExchangeMethodInvocationHandler(httpExchangeMethods, requestFactory, exchanger));
    }

    private Map<Method, HttpExchangeMethod> resolveServiceMethods() {
        return null;
    }

}
