package com.jn.agileway.web.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.http.HttpStatus;

/**
 * @see RestActionException
 */
public class RestActionExceptionHandlerDefinition {
    @NonNull
    private Class exceptionClass;

    private boolean supportExtends = false;

    @NonNull
    private int defaultStatusCode = 500;

    @Nullable
    private String defaultErrorCode;

    @Nullable
    private String defaultErrorMessage;

    public Class getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(Class exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public boolean isSupportExtends() {
        return supportExtends;
    }

    public void setSupportExtends(boolean supportExtends) {
        this.supportExtends = supportExtends;
    }

    public int getDefaultStatusCode() {
        return defaultStatusCode;
    }

    public void setDefaultStatusCode(int defaultStatusCode) {
        this.defaultStatusCode = defaultStatusCode;
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

    public boolean isValid(){
        return exceptionClass!=null && HttpStatus.resolve(defaultStatusCode)!=null;
    }
}
