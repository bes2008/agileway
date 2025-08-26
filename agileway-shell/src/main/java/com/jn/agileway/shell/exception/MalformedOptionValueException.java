package com.jn.agileway.shell.exception;

/**
 * 当命令的选项值不对时
 */
public class MalformedOptionValueException extends RuntimeException{
    public MalformedOptionValueException() {
        super();
    }

    public MalformedOptionValueException(String message) {
        super(message);
    }

    public MalformedOptionValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedOptionValueException(Throwable cause) {
        super(cause);
    }

    public MalformedOptionValueException(String message, Throwable cause,
                                          boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}