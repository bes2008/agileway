package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ApacheUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest {
    private HttpUriRequest request;
    private HttpClient underlyingClient;

    public ApacheUnderlyingHttpRequest(HttpClient client, HttpUriRequest request) {
        this.underlyingClient = client;
        this.request = request;
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(request.getMethod());
    }

    @Override
    public URI getUri() {
        return request.getURI();
    }

    @Override
    public OutputStream getContent() throws IOException {
        return null;
    }


    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        HttpResponse response = this.underlyingClient.execute(request);
        return null;
    }

}
