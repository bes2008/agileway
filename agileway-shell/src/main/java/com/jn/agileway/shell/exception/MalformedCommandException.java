package com.jn.agileway.shell.exception;

public class MalformedCommandException extends RuntimeException {

    public MalformedCommandException() {
        super();
    }

    public MalformedCommandException(String message) {
        super(message);
    }

    public MalformedCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedCommandException(Throwable cause) {
        super(cause);
    }

    public MalformedCommandException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
