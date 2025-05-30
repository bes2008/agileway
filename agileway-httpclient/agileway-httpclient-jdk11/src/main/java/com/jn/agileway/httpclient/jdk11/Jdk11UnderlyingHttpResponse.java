package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

class Jdk11UnderlyingHttpResponse implements UnderlyingHttpResponse {
    private URI uri;
    private HttpMethod method;
    private ByteArrayInputStream content;
    private HttpHeaders headers;
    int statusCode;

    Jdk11UnderlyingHttpResponse(HttpMethod method, URI uri, HttpHeaders headers, int statusCode, ByteArrayInputStream content) {
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        this.statusCode = statusCode;
        this.content = content;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getPayload() {
        return content;
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        return headers;
    }
}
