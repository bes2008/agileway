package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;

import java.util.List;

public class HttpResponsePayloadReadingInterceptor implements HttpResponseInterceptor {
    private List<HttpResponseContentReader> responseContentReaders;

    @Override
    public void intercept(HttpResponse response) {

    }
}
