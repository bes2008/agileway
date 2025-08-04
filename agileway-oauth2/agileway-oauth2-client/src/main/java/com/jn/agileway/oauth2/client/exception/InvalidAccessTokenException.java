package com.jn.agileway.oauth2.client.exception;

public class InvalidAccessTokenException extends OAuth2Exception {
    public InvalidAccessTokenException(String message) {
        super(message);
    }

    public InvalidAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
