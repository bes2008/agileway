package com.jn.agileway.httpclient.core.error;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.error.exception.HttpRequestClientErrorException;
import com.jn.agileway.httpclient.core.error.exception.HttpRequestServerErrorException;
import com.jn.agileway.httpclient.core.error.exception.MethodNotAllowedRequestException;

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
                throw new BadHttpRequestException(httpResponse.getMethod(), httpResponse.getUri(), httpResponse.getErrorMessage());
            }
            if (statusCode == 405) {
                throw new MethodNotAllowedRequestException(httpResponse.getMethod(), httpResponse.getUri(), httpResponse.getErrorMessage());
            }
            throw new HttpRequestClientErrorException(httpResponse.getMethod(), httpResponse.getUri(), statusCode, httpResponse.getErrorMessage());
        }
        if (statusCode >= 500) {
            throw new HttpRequestServerErrorException(httpResponse.getMethod(), httpResponse.getUri(), statusCode, httpResponse.getErrorMessage());
        }
    }
}
