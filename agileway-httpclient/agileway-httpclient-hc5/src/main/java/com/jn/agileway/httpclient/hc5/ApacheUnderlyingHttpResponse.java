package com.jn.agileway.httpclient.hc5;

import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ApacheUnderlyingHttpResponse extends BaseHttpMessage<InputStream> implements UnderlyingHttpResponse {
    private CloseableHttpResponse response;

    public ApacheUnderlyingHttpResponse(HttpMethod method, URI uri, CloseableHttpResponse response) {
        this.response = response;
        this.method = method;
        this.uri = uri;
    }

    @Override
    public int getStatusCode() {
        return this.response.getCode();
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
        HttpHeaders httpHeaders = super.getHttpHeaders();
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
            this.headers.setProtocolHeaders(httpHeaders);

            Header[] underlyingHeaders = this.response.getHeaders();
            for (Header underlyingHeader : underlyingHeaders) {
                httpHeaders.add(underlyingHeader.getName(), underlyingHeader.getValue());
            }
        }
        return httpHeaders;
    }
}
