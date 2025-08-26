package com.jn.agileway.shell.exception;

public class UnsupportedCollectionException extends RuntimeException{
    public UnsupportedCollectionException() {
        super();
    }

    public UnsupportedCollectionException(String message) {
        super(message);
    }

    public UnsupportedCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedCollectionException(Throwable cause) {
        super(cause);
    }

    public UnsupportedCollectionException(String message, Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
