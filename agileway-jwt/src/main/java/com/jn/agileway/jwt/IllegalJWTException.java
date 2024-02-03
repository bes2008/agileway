package com.jn.agileway.jwt;

public class IllegalJWTException extends RuntimeException{
    public IllegalJWTException() {
        super();
    }

    public IllegalJWTException(String message) {
        super(message);
    }

    public IllegalJWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalJWTException(Throwable cause) {
        super(cause);
    }
}
