package com.jn.agileway.httpclient.core.exception;

import com.jn.langx.text.StringTemplates;

public class HttpRequestClientErrorException extends RuntimeException {
    private int statusCode;
    private String statusText;

    public HttpRequestClientErrorException(int statusCode, String statusText) {
        super(StringTemplates.formatWithPlaceholder("request error: {}, {}", statusCode, statusText));
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
