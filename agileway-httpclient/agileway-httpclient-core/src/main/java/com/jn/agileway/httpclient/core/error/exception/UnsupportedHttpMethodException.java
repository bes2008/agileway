package com.jn.agileway.httpclient.core.error.exception;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class UnsupportedHttpMethodException extends MethodNotAllowedRequestException {
    public UnsupportedHttpMethodException(HttpMethod method, URI uri, String message) {
        super(method, uri, message);
    }
}
