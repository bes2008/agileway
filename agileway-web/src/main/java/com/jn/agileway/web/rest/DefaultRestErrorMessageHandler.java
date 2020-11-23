package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Emptys;

import java.util.Locale;

public class DefaultRestErrorMessageHandler implements RestErrorMessageHandler {
    /**
     * 默认的出错时的 http status code
     */
    private int defaultErrorStatusCode = 500;

    private String defaultErrorCode = "UNKNOWN";
    private String defaultErrorMessage = "UNKNOWN";

    @Override
    public void handler(Locale locale, RestRespBody respBody) {
        if (Emptys.isEmpty(respBody.getErrorCode())) {
            respBody.setErrorCode(defaultErrorCode);
        }
        if (Emptys.isEmpty(respBody.getErrorMessage())) {
            respBody.setErrorMessage(defaultErrorMessage);
        }
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
}
