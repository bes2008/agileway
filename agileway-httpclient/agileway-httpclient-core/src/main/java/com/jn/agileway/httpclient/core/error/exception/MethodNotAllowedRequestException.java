package com.jn.agileway.httpclient.core.error.exception;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class MethodNotAllowedRequestException extends HttpRequestClientErrorException {
    public MethodNotAllowedRequestException(HttpMethod method, URI uri, String statusText) {
        super(method, uri, 405, statusText);
    }
}
