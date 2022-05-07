package com.jn.agileway.http.rest;

public class GlobalRestExceptionHandlerProperties {
    /**
     * 是否根据 异常链扫描
     * <p>
     * 这个配置存在的意义: 有人喜欢把真正的异常用RuntimeException包装
     */
    private boolean causeScanEnabled = true;
    private int defaultErrorStatusCode = 500;
    private String defaultErrorCode = "UNKNOWN";
    private String defaultErrorMessage = "UNKNOWN";
    private boolean exceptionExtendsScanEnabled = true;
    private boolean writeUnifiedResponse = true;
    private boolean logStack = true;
    private boolean writeStackToResponse = false;

    public boolean isCauseScanEnabled() {
        return causeScanEnabled;
    }

    public void setCauseScanEnabled(boolean causeScanEnabled) {
        this.causeScanEnabled = causeScanEnabled;
    }

    public int getDefaultErrorStatusCode() {
        return defaultErrorStatusCode;
    }

    public void setDefaultErrorStatusCode(int defaultErrorStatusCode) {
        this.defaultErrorStatusCode = defaultErrorStatusCode;
    }

    public String getDefaultErrorCode() {
        return defaultErrorCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public boolean isExceptionExtendsScanEnabled() {
        return exceptionExtendsScanEnabled;
    }

    public void setExceptionExtendsScanEnabled(boolean exceptionExtendsScanEnabled) {
        this.exceptionExtendsScanEnabled = exceptionExtendsScanEnabled;
    }

    public boolean isWriteUnifiedResponse() {
        return writeUnifiedResponse;
    }

    public void setWriteUnifiedResponse(boolean writeUnifiedResponse) {
        this.writeUnifiedResponse = writeUnifiedResponse;
    }

    public boolean isLogStack() {
        return logStack;
    }

    public void setLogStack(boolean logStack) {
        this.logStack = logStack;
    }

    public boolean isWriteStackToResponse() {
        return writeStackToResponse;
    }

    public void setWriteStackToResponse(boolean writeStackToResponse) {
        this.writeStackToResponse = writeStackToResponse;
    }
}
