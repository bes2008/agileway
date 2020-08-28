package com.jn.agileway.web.rest;

public class GlobalRestExceptionHandlerProperties {
    private boolean causeScanEnabled = false;
    private int defaultErrorStatusCode = 500;
    private String defaultErrorCode;
    private String defaultErrorMessage;
    private boolean exceptionExtendsScanEnabled;

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
}
