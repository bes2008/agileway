package com.jn.agileway.shell.result;

public class CmdExecResult {
    private int exitCode;
    private String errorMessage;
    private Throwable exception;
    private String cmdResult;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCmdResult() {
        return cmdResult;
    }

    public void setCmdResult(String cmdResult) {
        this.cmdResult = cmdResult;
    }
}
