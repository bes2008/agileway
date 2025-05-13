package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ApacheUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest {
    private HttpUriRequest request;
    private CloseableHttpClient underlyingClient;

    public ApacheUnderlyingHttpRequest(CloseableHttpClient client, HttpUriRequest request) {
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
        CloseableHttpResponse response = this.underlyingClient.execute(request);
        return new ApacheUnderlyingHttpResponse(this.getMethod(), this.getUri(), response);
    }

}
