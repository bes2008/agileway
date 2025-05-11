package com.jn.agileway.httpclient.core.exception;

public class BadHttpRequestException extends HttpRequestClientErrorException {

    public BadHttpRequestException(String statusText) {
        super(400, statusText);
    }
}
