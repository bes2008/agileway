package com.jn.agileway.httpclient.core.exception;

import com.jn.langx.text.StringTemplates;

public class HttpRequestServerErrorException extends RuntimeException {
    private int statusCode;
    private String statusText;

    public HttpRequestServerErrorException(int statusCode, String statusText) {
        super(StringTemplates.formatWithPlaceholder("request handled error: {}, {}", statusCode, statusText));
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
