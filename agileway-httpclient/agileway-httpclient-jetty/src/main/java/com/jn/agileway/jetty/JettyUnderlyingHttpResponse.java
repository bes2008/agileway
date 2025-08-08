package com.jn.agileway.jetty;

import com.jn.agileway.eipchannel.core.message.MessageHeaders;
import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.InputStream;
import java.net.URI;

class JettyUnderlyingHttpResponse extends BaseHttpMessage<InputStream> implements UnderlyingHttpResponse {
    private int statusCode;

    JettyUnderlyingHttpResponse(HttpMethod method, URI uri, HttpHeaders headers, int statusCode, InputStream payload) {
        this.method = method;
        this.uri = uri;
        this.headers = new MessageHeaders<HttpHeaders>(null, headers);
        this.payload = payload;
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public void close() {

    }
}
