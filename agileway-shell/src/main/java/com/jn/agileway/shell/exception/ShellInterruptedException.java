package com.jn.agileway.shell.exception;

/**
 * 运行中的shell被强行中断
 */
public class ShellInterruptedException extends RuntimeException {

    private int exitCode;

    public ShellInterruptedException(String msg, int exitCode) {
       this(msg,exitCode,null);
    }
    public ShellInterruptedException(String msg, int exitCode, Throwable cause) {
        super(msg,cause);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

}
