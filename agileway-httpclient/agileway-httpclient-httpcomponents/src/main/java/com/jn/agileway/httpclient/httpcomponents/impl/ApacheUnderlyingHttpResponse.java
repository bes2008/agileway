package com.jn.agileway.httpclient.httpcomponents.impl;


import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ApacheUnderlyingHttpResponse implements UnderlyingHttpResponse {
    private HttpResponse response;
    private HttpMethod method;
    private URI uri;
    private HttpHeaders headers;

    public ApacheUnderlyingHttpResponse(HttpMethod method, URI uri, HttpResponse response) {
        this.response = response;
        this.method = method;
        this.uri = uri;
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
        return this.response.getStatusLine().getStatusCode();
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getContent() throws IOException {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            Header[] underlyingHeaders = this.response.getAllHeaders();

            HttpHeaders httpHeaders = new HttpHeaders();
            for (Header underlyingHeader : underlyingHeaders) {
                HeaderElement[] elements = underlyingHeader.getElements();
                for (HeaderElement element : elements) {
                    httpHeaders.add(underlyingHeader.getName(), element.getValue());
                }

            }
        }
        return this.headers;
    }
}
