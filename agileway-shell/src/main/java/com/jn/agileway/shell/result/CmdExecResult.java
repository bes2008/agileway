package com.jn.agileway.shell.result;

public class CmdExecResult {
    private int exitCode;
    private String errorMessage;
    private Throwable exception;
    private Object methodInvocationResult;

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

    public Object getMethodInvocationResult() {
        return methodInvocationResult;
    }

    public void setMethodInvocationResult(Object methodInvocationResult) {
        this.methodInvocationResult = methodInvocationResult;
    }
}
