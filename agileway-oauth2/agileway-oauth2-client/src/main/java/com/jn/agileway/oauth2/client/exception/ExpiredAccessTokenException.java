package com.jn.agileway.oauth2.client.exception;

public class ExpiredAccessTokenException extends InvalidAccessTokenException {
    public ExpiredAccessTokenException(String message) {
        super(message);
    }
}
