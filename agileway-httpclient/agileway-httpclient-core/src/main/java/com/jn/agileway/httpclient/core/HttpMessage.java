package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public interface HttpMessage<T> {
    URI getUri();

    HttpMethod getMethod();

    HttpHeaders getHeaders();

    T getContent();
}
