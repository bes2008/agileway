package com.jn.agileway.http.rr;

public interface HttpRRFactorySupplier {
    HttpRequestFactory getHttpRequestFactory();

    HttpResponseFactory getHttpResponseFactory();
}
