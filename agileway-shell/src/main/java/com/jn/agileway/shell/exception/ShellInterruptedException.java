package com.jn.agileway.shell.exception;

/**
 * 运行中的shell被强行中断
 */
public class ShellInterruptedException extends RuntimeException {

    public ShellInterruptedException() {
        super();
    }

    public ShellInterruptedException(String cmdKey) {
        super(cmdKey);
    }

    public ShellInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellInterruptedException(Throwable cause) {
        super(cause);
    }

    public ShellInterruptedException(String message, Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
