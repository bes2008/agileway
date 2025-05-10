package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * 代表了Http响应，它是提供给用户直接使用的
 *
 * @param <T>
 */
public class HttpResponse<T> {
    private URI uri;
    private HttpMethod method;
    private int statusCode;
    private HttpHeaders httpHeaders;
    private T body;
    private String errorMessage;

    public HttpResponse(UnderlyingHttpResponse response) {
        this(response, null);
    }

    public HttpResponse(UnderlyingHttpResponse response, T body) {
        this(response, body, false);
    }

    public HttpResponse(UnderlyingHttpResponse response, T body, boolean readIfBodyAbsent) {
        this.uri = response.getUri();
        this.method = response.getMethod();
        this.statusCode = response.getStatusCode();
        this.httpHeaders = response.getHeaders();

        if (body != null) {
            this.body = body;
        } else if (readIfBodyAbsent) {
            try {
                InputStream inputStream = response.getBody();
                this.body = (T) IOs.toByteArray(inputStream);
            } catch (IOException e) {
                throw Throwables.wrapAsRuntimeIOException(e);
            }
        }
    }

    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public T getBody() {
        return body;
    }


}
