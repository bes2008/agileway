package com.jn.agileway.httpclient.core;

public class InvalidHttpRequestException extends RuntimeException {
    public InvalidHttpRequestException(String message) {
        super(message);
    }

    public InvalidHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
