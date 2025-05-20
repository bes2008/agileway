package com.jn.agileway.httpclient.core.exception;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public class HttpRequestClientErrorException extends RuntimeException {
    private HttpMethod method;
    private URI uri;
    private int statusCode;
    private String statusText;

    public HttpRequestClientErrorException(HttpMethod method, URI uri, int statusCode, String statusText) {
        super(StringTemplates.formatWithPlaceholder("request error: {} {}, response: {}, {} ", method, uri, statusCode, statusText));
        this.uri = uri;
        this.method = method;
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
