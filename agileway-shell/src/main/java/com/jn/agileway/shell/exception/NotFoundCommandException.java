package com.jn.agileway.shell.exception;

/**
 * 当未找到命令时抛出
 */
public class NotFoundCommandException extends RuntimeException {

    public NotFoundCommandException() {
        super();
    }

    public NotFoundCommandException(String cmdKey) {
        super(cmdKey);
    }

    public NotFoundCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCommandException(Throwable cause) {
        super(cause);
    }

    public NotFoundCommandException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
