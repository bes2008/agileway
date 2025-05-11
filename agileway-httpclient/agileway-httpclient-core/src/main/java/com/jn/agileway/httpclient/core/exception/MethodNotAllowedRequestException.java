package com.jn.agileway.httpclient.core.exception;

public class MethodNotAllowedRequestException extends HttpRequestClientErrorException {
    public MethodNotAllowedRequestException(String statusText) {
        super(405, statusText);
    }
}
