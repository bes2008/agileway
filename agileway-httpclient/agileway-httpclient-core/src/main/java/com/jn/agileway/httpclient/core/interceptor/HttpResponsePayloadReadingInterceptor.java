package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;

import java.util.List;

public class HttpResponsePayloadReadingInterceptor implements HttpResponseInterceptor {
    private List<HttpResponsePayloadReader> responseContentReaders;

    @Override
    public void intercept(HttpResponse response) {

    }
}
