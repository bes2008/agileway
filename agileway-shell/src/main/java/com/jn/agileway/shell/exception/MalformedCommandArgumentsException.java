package com.jn.agileway.shell.exception;

public class MalformedCommandArgumentsException extends RuntimeException{
    public MalformedCommandArgumentsException() {
        super();
    }

    public MalformedCommandArgumentsException(String message) {
        super(message);
    }

    public MalformedCommandArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedCommandArgumentsException(Throwable cause) {
        super(cause);
    }

    public MalformedCommandArgumentsException(String message, Throwable cause,
                                              boolean enableSuppression,
                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
