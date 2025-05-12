package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class HttpRequest<T> {
    private URI uri;
    private HttpMethod method;
    private HttpHeaders headers;
    private T content;


    HttpRequest(URI uri, HttpMethod method, HttpHeaders headers, T body) {
        this.uri = uri;
        this.method = method;
        this.headers = headers == null ? new HttpHeaders() : headers;
        this.content = body;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
