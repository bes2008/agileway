package com.jn.agileway.httpclient.core.exception;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class HttpRequestServerErrorException extends RuntimeException {
    private HttpMethod method;
    private URI uri;
    private int statusCode;
    private String statusText;

    public HttpRequestServerErrorException(HttpMethod method, URI uri, int statusCode, String statusText) {
        super(StringTemplates.formatWithPlaceholder("request handled error, request: {} {}  response: {}, {}", method, uri, statusCode, statusText));
        this.method = method;
        this.uri = uri;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
