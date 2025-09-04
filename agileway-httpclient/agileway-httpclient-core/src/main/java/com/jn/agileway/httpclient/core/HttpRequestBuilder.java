package com.jn.agileway.httpclient.core;

import com.jn.langx.Builder;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public class HttpRequestBuilder implements Builder<HttpRequest> {
    private HttpMethod method;
    private String uriTemplate;
    private Charset uriEncoding;
    private MultiValueMap<String, Object> queryParams;
    private Map<String, Object> uriVariables;
    private HttpHeaders headers = new HttpHeaders();
    private Object payload;

    public HttpRequestBuilder() {

    }

    public HttpRequestBuilder(HttpMethod method, String uriTemplate) {
        this.method = method;
        this.uriTemplate = uriTemplate;
    }

    public HttpRequestBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder uriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    public HttpRequestBuilder uriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
        return this;
    }

    public HttpRequestBuilder queryParams(MultiValueMap<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public HttpRequestBuilder uriVariables(Map<String, Object> uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    public HttpRequestBuilder setHeaders(MultiValueMap<String, String> headers) {
        for (String name : headers.keySet()) {
            Collection<String> values = headers.get(name);
            this.headers.remove(name);
            for (String value : values) {
                this.headers.add(name, value);
            }
        }
        return this;
    }

    public HttpRequestBuilder addHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    public HttpRequestBuilder setHeader(String name, String value) {
        this.headers.set(name, value);
        return this;
    }

    public HttpRequestBuilder payload(Object payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public HttpRequest<?> build() {
        return HttpRequest.create(method, uriTemplate, uriEncoding, queryParams, uriVariables, headers, payload);
    }
}
