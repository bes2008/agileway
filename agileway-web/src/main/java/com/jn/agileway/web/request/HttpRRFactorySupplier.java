package com.jn.agileway.web.request;

public interface HttpRRFactorySupplier {
    HttpRequestFactory getHttpRequestFactory();

    HttpResponseFactory getHttpResponseFactory();
}
