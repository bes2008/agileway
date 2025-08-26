package com.jn.agileway.httpclient.core.error.exception;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class HttpTimeoutException extends RuntimeException {
    private final HttpMethod method;
    private final URI uri;

    public HttpTimeoutException(HttpMethod method, URI uri, String error) {
        super(error);
        this.method = method;
        this.uri = uri;
    }
}
