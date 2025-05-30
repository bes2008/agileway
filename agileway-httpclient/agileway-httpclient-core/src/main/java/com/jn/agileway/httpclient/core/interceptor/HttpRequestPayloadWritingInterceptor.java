package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;

import java.util.List;

public class HttpRequestPayloadWritingInterceptor implements HttpRequestInterceptor {

    private List<HttpRequestContentWriter> requestContentWriters;

    @Override
    public void intercept(HttpRequest request) {

    }
}
