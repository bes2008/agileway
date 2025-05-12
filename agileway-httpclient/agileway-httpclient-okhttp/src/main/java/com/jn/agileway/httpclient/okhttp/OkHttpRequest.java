package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class OkHttpRequest extends AbstractUnderlyingHttpRequest {
    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
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
    public OutputStream getContent() throws IOException {
        return null;
    }
}
