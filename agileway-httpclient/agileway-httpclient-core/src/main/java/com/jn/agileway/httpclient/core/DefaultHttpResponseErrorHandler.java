package com.jn.agileway.httpclient.core;

public class DefaultHttpResponseErrorHandler implements HttpResponseErrorHandler {
    @Override
    public boolean isError(HttpResponse<?> response) {
        return response.hasError();
    }

    @Override
    public void handle(HttpResponse<?> httpResponse) {

    }
}
