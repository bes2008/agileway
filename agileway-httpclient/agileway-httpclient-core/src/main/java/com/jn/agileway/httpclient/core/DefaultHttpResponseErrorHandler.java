package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.exception.HttpRequestClientErrorException;
import com.jn.agileway.httpclient.core.exception.HttpRequestServerErrorException;
import com.jn.agileway.httpclient.core.exception.MethodNotAllowedRequestException;

public class DefaultHttpResponseErrorHandler implements HttpResponseErrorHandler {
    @Override
    public boolean isError(HttpResponse<?> response) {
        return response.hasError();
    }

    @Override
    public void handle(HttpResponse<?> httpResponse) {
        if (!httpResponse.hasError()) {
            return;
        }

        int statusCode = httpResponse.getStatusCode();
        if (statusCode >= 400 && statusCode < 500) {
            if (statusCode == 400) {
                throw new BadHttpRequestException(httpResponse.getErrorMessage());
            }
            if (statusCode == 405) {
                throw new MethodNotAllowedRequestException(httpResponse.getErrorMessage());
            }
            throw new HttpRequestClientErrorException(statusCode, httpResponse.getErrorMessage());
        }
        if (statusCode >= 500) {
            throw new HttpRequestServerErrorException(statusCode, httpResponse.getErrorMessage());
        }
    }
}
