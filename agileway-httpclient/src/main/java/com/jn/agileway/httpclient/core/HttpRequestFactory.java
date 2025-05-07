package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

public interface HttpRequestFactory {
    HttpRequest create(HttpMethod method, URI uri) throws Exception;
}
