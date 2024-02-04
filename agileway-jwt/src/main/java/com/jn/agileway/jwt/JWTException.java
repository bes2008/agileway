package com.jn.agileway.jwt;

public class JWTException extends RuntimeException{
    public JWTException() {
        super();
    }

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public JWTException(Throwable cause) {
        super(cause);
    }
}
