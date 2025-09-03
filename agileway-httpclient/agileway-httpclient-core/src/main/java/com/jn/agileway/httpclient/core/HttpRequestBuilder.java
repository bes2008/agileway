package com.jn.agileway.httpclient.core;

import com.jn.langx.Builder;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequestBuilder implements Builder<HttpRequest> {
    private HttpMethod method;
    private String uriTemplate;
    private Charset uriEncoding;
    private MultiValueMap<String, Object> queryParams;
    private Map<String, Object> uriVariables;
    private HttpHeaders headers;
    private Object payload;

    public HttpRequestBuilder() {

    }

    public HttpRequestBuilder(HttpMethod method, String uriTemplate) {
        this.method = method;
        this.uriTemplate = uriTemplate;
    }

    public HttpRequestBuilder withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder withUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    public HttpRequestBuilder withUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
        return this;
    }

    public HttpRequestBuilder withQueryParams(MultiValueMap<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public HttpRequestBuilder withUriVariables(Map<String, Object> uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    public HttpRequestBuilder withHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder withHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
        }
        this.headers.set(name, value);
        return this;
    }

    public HttpRequestBuilder withPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public HttpRequest build() {
        return HttpRequest.create(method, uriTemplate, uriEncoding, queryParams, uriVariables, headers, payload);
    }
}
