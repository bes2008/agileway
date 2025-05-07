package com.jn.agileway.httpclient.apache;

import com.jn.agileway.httpclient.core.AbstractHttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

public class ApacheHttpClientRequest extends AbstractHttpRequest {
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
