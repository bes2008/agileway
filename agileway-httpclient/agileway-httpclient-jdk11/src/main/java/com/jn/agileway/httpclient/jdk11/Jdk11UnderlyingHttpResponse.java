package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

class Jdk11UnderlyingHttpResponse implements UnderlyingHttpResponse {
    private URI uri;
    private HttpMethod method;
    ByteArrayInputStream content;
    final HttpHeaders headers = new HttpHeaders();
    int statusCode;

    Jdk11UnderlyingHttpResponse(HttpMethod method, URI uri) {
        this.uri = uri;
        this.method = method;
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
    public InputStream getContent() throws IOException {
        return content;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
