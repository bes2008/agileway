package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.Factory;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;

import java.util.Map;

public class HttpRequestFactory implements Factory<Object[], HttpRequest<?>> {
    private HttpExchangeMethod httpExchangeMethod;

    public HttpRequestFactory(HttpExchangeMethod httpExchangeMethod) {
        this.httpExchangeMethod = httpExchangeMethod;
    }

    public HttpRequest<?> get(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = resolveQueryParams(methodArgs);
        Map<String, Object> uriVariables = resolveUriVariables(methodArgs);
        HttpHeaders headers = resolveHeaders(methodArgs);
        Object body = resolveBody(methodArgs);
        return HttpRequest.create(
                httpExchangeMethod.getHttpMethod(),
                httpExchangeMethod.getUriTemplate(),
                queryParams,
                uriVariables,
                headers,
                body);
    }

    private MultiValueMap<String, Object> resolveQueryParams(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = null;
        return queryParams;
    }

    private Map<String, Object> resolveUriVariables(Object[] methodArgs) {
        Map<String, Object> uriVariables = null;
        return uriVariables;
    }


    private Object resolveBody(Object[] methodArgs) {
        Object body = null;
        return body;
    }

    private HttpHeaders resolveHeaders(Object[] methodArgs) {
        HttpHeaders headers = null;
        return headers;
    }

}
