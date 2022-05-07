package com.jn.agileway.web.servlet;

import com.jn.agileway.web.request.HttpRRFactorySupplier;
import com.jn.agileway.web.request.HttpRequestFactory;
import com.jn.agileway.web.request.HttpResponseFactory;


public class ServletHttpRRFactorySupplier implements HttpRRFactorySupplier {
    @Override
    public HttpRequestFactory getHttpRequestFactory() {
        return new ServletHttpRequestFactory();
    }

    @Override
    public HttpResponseFactory getHttpResponseFactory() {
        return new ServletHttpResponseFactory();
    }
}
