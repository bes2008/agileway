package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.AbstractHttpRequest;
import com.jn.agileway.httpclient.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

public class OkHttpRequest extends AbstractHttpRequest {
    @Override
    protected HttpResponse exchangeInternal() throws IOException {
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

}
