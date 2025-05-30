package com.jn.agileway.httpclient.httpcomponents.httpexchange;


import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

class ApacheUnderlyingHttpResponse implements UnderlyingHttpResponse {
    private CloseableHttpResponse response;
    private HttpMethod method;
    private URI uri;
    private HttpHeaders headers;

    public ApacheUnderlyingHttpResponse(HttpMethod method, URI uri, CloseableHttpResponse response) {
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
        try {
            this.response.close();
        } catch (IOException ex) {
            // ignore it
        }
    }

    @Override
    public InputStream getPayload() {
        HttpEntity body = this.response.getEntity();
        if (body != null) {
            try {
                return body.getContent();
            } catch (IOException ex) {
                throw Throwables.wrapAsRuntimeIOException(ex);
            }
        }
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            Header[] underlyingHeaders = this.response.getAllHeaders();

            HttpHeaders httpHeaders = new HttpHeaders();
            for (Header underlyingHeader : underlyingHeaders) {
                httpHeaders.add(underlyingHeader.getName(), underlyingHeader.getValue());
            }
            this.headers = httpHeaders;
        }
        return this.headers;
    }
}
