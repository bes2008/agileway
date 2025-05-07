package com.jn.agileway.httpclient.jetty;

import com.jn.agileway.httpclient.AbstractHttpRequest;
import com.jn.agileway.httpclient.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

public class JettyHttpRequest extends AbstractHttpRequest {
    @Override
    protected Promise<HttpResponse> doExecute() throws IOException {
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return null;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }
}
