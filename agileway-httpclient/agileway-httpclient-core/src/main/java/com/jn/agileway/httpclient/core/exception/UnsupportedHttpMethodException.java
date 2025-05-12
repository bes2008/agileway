package com.jn.agileway.httpclient.core.exception;

public class UnsupportedHttpMethodException extends HttpRequestClientErrorException {
    public UnsupportedHttpMethodException(String message) {
        super(405, message);
    }
}
