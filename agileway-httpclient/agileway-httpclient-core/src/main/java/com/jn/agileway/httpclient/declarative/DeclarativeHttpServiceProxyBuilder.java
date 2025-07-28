package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.agileway.httpclient.declarative.anno.*;
import com.jn.langx.Builder;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

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
        Map<Method, HttpExchangeMethod> httpExchangeMethods = new HashMap<Method, HttpExchangeMethod>();
        List<Class> interfaces = new ArrayList<>(Reflects.getAllInterfaces(serviceInterface));
        Collections.reverse(interfaces);
        interfaces.add(serviceInterface);
        for (Class interfaceClass : interfaces) {
            if (!Reflects.hasAnnotation(interfaceClass, HttpExchange.class)) {
                continue;
            }
            Collection<Method> declaredMethods = Reflects.getAllDeclaredMethods(interfaceClass, false);
            for (Method method : declaredMethods) {
                if (!Reflects.isAnnotatedWith(method, Get.class, Post.class, Put.class, Delete.class, Patch.class)) {
                    continue;
                }
                HttpExchangeMethod httpExchangeMethod = this.methodResolver.resolve(method);
                httpExchangeMethods.put(method, httpExchangeMethod);
            }
        }
        return httpExchangeMethods;
    }

}
