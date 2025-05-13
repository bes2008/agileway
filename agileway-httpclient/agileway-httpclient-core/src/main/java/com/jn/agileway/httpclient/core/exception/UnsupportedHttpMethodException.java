package com.jn.agileway.httpclient.core.exception;

public class UnsupportedHttpMethodException extends MethodNotAllowedRequestException {
    public UnsupportedHttpMethodException(String message) {
        super(message);
    }
}
