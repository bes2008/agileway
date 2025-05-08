package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;

public class HttpResponseEntity<T> {
    private HttpResponse response;
    private T body;

    HttpResponseEntity(HttpResponse response, T body) {
        this.response = response;
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public T getBody() {
        return body;
    }
}
