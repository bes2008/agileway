package com.jn.agileway.httpclient.httpcomponents.impl;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

class ApacheUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest {
    private HttpUriRequest request;
    private CloseableHttpClient underlyingClient;
    private BufferedHttpEntity contentEntity;
    private HttpHeaders httpHeaders;

    ApacheUnderlyingHttpRequest(CloseableHttpClient client, HttpUriRequest request, HttpHeaders httpHeaders) {
        this.underlyingClient = client;
        this.request = request;
        this.httpHeaders = httpHeaders;
        this.addHeaders(httpHeaders);
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
        if (this.contentEntity == null) {
            this.contentEntity = new BufferedHttpEntity(this.httpHeaders.getContentType().toString(), ContentEncoding.ofName(this.httpHeaders.getFirst("Content-Encoding")));
        }
        return this.contentEntity;
    }


    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        writeHeaders();
        if (this.request instanceof HttpEntityEnclosingRequestBase && this.contentEntity != null) {
            HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) this.request;
            request.setEntity(this.contentEntity);
        }
        CloseableHttpResponse response = this.underlyingClient.execute(request);
        return new ApacheUnderlyingHttpResponse(this.getMethod(), this.getUri(), response);
    }

    @Override
    protected void addHeaderToUnderlying(String headerName, String headerValue) {
        this.request.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(String headerName, String headerValue) {
        this.request.removeHeaders(headerName);
        this.request.addHeader(headerName, headerValue);
    }
}
