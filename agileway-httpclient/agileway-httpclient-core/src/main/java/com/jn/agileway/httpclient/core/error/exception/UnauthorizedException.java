package com.jn.agileway.httpclient.core.error.exception;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class UnauthorizedException extends HttpRequestClientErrorException {
    public UnauthorizedException(HttpMethod method, URI uri, int statusCode, String statusText) {
        super(method, uri, statusCode, statusText);
    }
}
