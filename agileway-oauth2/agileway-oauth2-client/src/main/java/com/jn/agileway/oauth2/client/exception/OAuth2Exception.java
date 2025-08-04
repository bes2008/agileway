package com.jn.agileway.oauth2.client.exception;

public class OAuth2Exception extends RuntimeException {
    public OAuth2Exception(String message) {
        super(message);
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
