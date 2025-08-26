package com.jn.agileway.httpclient.core;

public interface HttpResponseMessage<T> extends HttpMessage<T> {
    int getStatusCode();
}
