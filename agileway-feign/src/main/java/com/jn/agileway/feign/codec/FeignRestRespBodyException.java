package com.jn.agileway.feign.codec;

import com.jn.langx.text.StringTemplates;
import feign.Response;

public class FeignRestRespBodyException extends RuntimeException {
    private String methodKey;
    private Response response;
    private String responseBody;

    public FeignRestRespBodyException(String methodKey, Response response) {
        this.methodKey = methodKey;
        this.response = response;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }


    public int getStatusCode() {
        return response.status();
    }


    public String getMethodKey() {
        return methodKey;
    }


    @Override
    public String getMessage() {
        return StringTemplates.formatWithPlaceholder("status {} reading {}; content: {}", getStatusCode(), methodKey, responseBody);
    }
}
