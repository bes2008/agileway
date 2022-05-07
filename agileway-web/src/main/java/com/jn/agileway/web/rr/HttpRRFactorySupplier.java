package com.jn.agileway.web.rr;

public interface HttpRRFactorySupplier {
    HttpRequestFactory getHttpRequestFactory();

    HttpResponseFactory getHttpResponseFactory();
}
