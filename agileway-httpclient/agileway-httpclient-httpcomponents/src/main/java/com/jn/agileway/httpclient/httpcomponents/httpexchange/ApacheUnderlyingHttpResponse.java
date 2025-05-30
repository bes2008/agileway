package com.jn.agileway.httpclient.httpcomponents.httpexchange;


import com.jn.agileway.httpclient.core.BaseHttpMessage;
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

class ApacheUnderlyingHttpResponse extends BaseHttpMessage<InputStream> implements UnderlyingHttpResponse {
    private CloseableHttpResponse response;

    public ApacheUnderlyingHttpResponse(HttpMethod method, URI uri, CloseableHttpResponse response) {
        this.response = response;
        this.method = method;
        this.uri = uri;
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
    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = super.getHttpHeaders();
        if (headers == null) {
            Header[] underlyingHeaders = this.response.getAllHeaders();

            HttpHeaders httpHeaders = new HttpHeaders();
            for (Header underlyingHeader : underlyingHeaders) {
                httpHeaders.add(underlyingHeader.getName(), underlyingHeader.getValue());
            }
            this.headers.setProtocolHeaders(httpHeaders);
        }
        return headers;
    }
}
