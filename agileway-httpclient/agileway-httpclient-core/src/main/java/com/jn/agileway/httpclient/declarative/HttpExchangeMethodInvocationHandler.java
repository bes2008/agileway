package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

class HttpExchangeMethodInvocationHandler implements InvocationHandler {

    private Map<Method, HttpExchangeMethod> httpExchangeMethods;
    private DeclarativeHttpRequestFactory declarativeHttpRequestFactory;

    private Exchanger exchanger;


    HttpExchangeMethodInvocationHandler(Map<Method, HttpExchangeMethod> httpExchangeMethods, DeclarativeHttpRequestFactory declarativeHttpRequestFactory, Exchanger httpExchanger) {
        this.httpExchangeMethods = httpExchangeMethods;
        this.declarativeHttpRequestFactory = declarativeHttpRequestFactory;
        this.exchanger = httpExchanger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpExchangeMethod httpExchangeMethod = httpExchangeMethods.get(method);
        if (httpExchangeMethod != null) {
            HttpRequest<?> request = declarativeHttpRequestFactory.get(args);
            Promise<HttpResponse<Object>> promise = exchanger.exchange(true, request, httpExchangeMethod.getExpectedResponseType());
            if (method.getReturnType() == HttpResponse.class) {
                return promise.await();
            } else {
                return promise.await().getPayload();
            }
        }
        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
    }
}
