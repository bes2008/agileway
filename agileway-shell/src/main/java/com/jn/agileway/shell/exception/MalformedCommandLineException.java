package com.jn.agileway.shell.exception;

public class MalformedCommandLineException extends RuntimeException{
    public MalformedCommandLineException() {
        super();
    }

    public MalformedCommandLineException(String message) {
        super(message);
    }

    public MalformedCommandLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedCommandLineException(Throwable cause) {
        super(cause);
    }

    public MalformedCommandLineException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
