package com.jn.agileway.httpclient.core.exception;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class BadHttpRequestException extends HttpRequestClientErrorException {

    public BadHttpRequestException(HttpMethod method, URI uri, String statusText) {
        super(method, uri, 400, statusText);
    }
}
