package com.jn.agileway.web.servlet;

import com.jn.agileway.web.rr.HttpRRFactorySupplier;
import com.jn.agileway.web.rr.HttpRequestFactory;
import com.jn.agileway.web.rr.HttpResponseFactory;


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
