package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.reflect.signature.TypeSignatures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

class HttpExchangeMethodInvocationHandler implements InvocationHandler {

    private Map<Method, HttpExchangeMethod> httpExchangeMethods;
    private String baseUri;
    private Exchanger exchanger;


    HttpExchangeMethodInvocationHandler(String baseUri, Map<Method, HttpExchangeMethod> httpExchangeMethods, Exchanger httpExchanger) {
        this.baseUri = baseUri;
        this.httpExchangeMethods = httpExchangeMethods;
        this.exchanger = httpExchanger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpExchangeMethod httpExchangeMethod = httpExchangeMethods.get(method);
        if (httpExchangeMethod != null) {
            HttpRequest<?> request = new DeclarativeHttpRequestFactory(baseUri, httpExchangeMethod).get(args);
            Promise<HttpResponse<Object>> promise = exchanger.exchange(true, request, httpExchangeMethod.getExpectedResponseType());
            if (method.getReturnType() == HttpResponse.class) {
                return promise.await();
            } else {
                return promise.await().getPayload();
            }
        }
        throw new UnsupportedOperationException(StringTemplates.formatWithPlaceholder("Method {} is not a http exchange method", TypeSignatures.toMethodSignature(method)));
    }
}
