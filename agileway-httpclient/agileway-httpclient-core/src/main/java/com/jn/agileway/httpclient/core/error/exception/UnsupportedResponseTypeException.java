package com.jn.agileway.httpclient.core.error.exception;

public class UnsupportedResponseTypeException extends RuntimeException {
    public UnsupportedResponseTypeException(String message, Exception e) {
        super(message, e);
    }
}
