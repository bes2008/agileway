package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public abstract class BaseHttpMessage<T> implements HttpMessage<T> {
    protected URI uri;
    protected HttpMethod method;
    protected HttpHeaders httpHeaders;
    protected T payload;

    public URI getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public T getPayload() {
        return payload;
    }
}
