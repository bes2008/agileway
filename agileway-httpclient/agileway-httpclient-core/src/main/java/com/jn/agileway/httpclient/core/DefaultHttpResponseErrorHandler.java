package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.exception.HttpRequestClientErrorException;

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
        if (statusCode < 500) {
            throw new HttpRequestClientErrorException(httpResponse.getErrorMessage());
        }
    }
}
