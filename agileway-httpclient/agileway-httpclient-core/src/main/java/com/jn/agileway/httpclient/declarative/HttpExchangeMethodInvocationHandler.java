package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.reflect.signature.TypeSignatures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;

class HttpExchangeMethodInvocationHandler implements InvocationHandler {

    private Map<Method, HttpExchangeMethod> httpExchangeMethods;
    private String baseUri;

    private Charset uriEncoding;
    private Exchanger exchanger;


    HttpExchangeMethodInvocationHandler(String baseUri, Charset uriEncoding, Map<Method, HttpExchangeMethod> httpExchangeMethods, Exchanger httpExchanger) {
        this.baseUri = baseUri;
        this.uriEncoding = uriEncoding;
        this.httpExchangeMethods = httpExchangeMethods;
        this.exchanger = httpExchanger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpExchangeMethod httpExchangeMethod = httpExchangeMethods.get(method);
        if (httpExchangeMethod != null) {
            HttpRequest<?> request = new DeclarativeHttpRequestFactory(baseUri, httpExchangeMethod).get(args);

            boolean async = method.getReturnType() == Promise.class;

            Promise<HttpResponse<Object>> promise = exchanger.exchange(async, request, httpExchangeMethod.getExpectedResponseType());
            if (method.getReturnType() == void.class) {
                return null;
            }
            if (method.getReturnType() == Promise.class) {
                return promise;
            }
            Object result = promise.await();
            if (method.getReturnType() == HttpResponse.class) {
                return result;
            } else {
                return ((HttpResponse) result).getPayload();
            }
        }
        throw new UnsupportedOperationException(StringTemplates.formatWithPlaceholder("Method {} is not a http exchange method", TypeSignatures.toMethodSignature(method)));
    }
}
