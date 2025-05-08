package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;

/**
 * 代表了Http响应，它是提供给用户直接使用的
 *
 * @param <T>
 */
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
