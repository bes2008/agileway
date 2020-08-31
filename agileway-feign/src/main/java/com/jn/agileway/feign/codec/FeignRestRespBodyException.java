package com.jn.agileway.feign.codec;

import com.jn.langx.text.StringTemplates;

public class FeignRestRespBodyException extends RuntimeException{
    private String responseBody;
    private int statusCode;
    private String methodKey;

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMethodKey() {
        return methodKey;
    }

    public void setMethodKey(String methodKey) {
        this.methodKey = methodKey;
    }

    @Override
    public String getMessage() {
        return StringTemplates.formatWithPlaceholder("status {} reading {}; content: {}", statusCode, methodKey, responseBody);
    }
}
