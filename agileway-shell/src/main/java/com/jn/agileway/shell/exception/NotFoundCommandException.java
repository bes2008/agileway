package com.jn.agileway.shell.exception;

public class NotFoundCommandException extends RuntimeException {

    public NotFoundCommandException() {
        super();
    }

    public NotFoundCommandException(String message) {
        super(message);
    }

    public NotFoundCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCommandException(Throwable cause) {
        super(cause);
    }

    protected NotFoundCommandException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
