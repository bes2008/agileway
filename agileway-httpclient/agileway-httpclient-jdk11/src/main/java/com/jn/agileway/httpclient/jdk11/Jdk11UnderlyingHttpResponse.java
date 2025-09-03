package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.eipchannel.core.message.MessageHeaders;
import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.InputStream;
import java.net.URI;

class Jdk11UnderlyingHttpResponse extends BaseHttpMessage<InputStream> implements UnderlyingHttpResponse {
    private int statusCode;

    Jdk11UnderlyingHttpResponse(HttpMethod method, URI uri, HttpHeaders headers, int statusCode, InputStream payload) {
        this.uri = uri;
        this.method = method;
        this.headers = new MessageHeaders<HttpHeaders>(null, headers);
        this.statusCode = statusCode;
        this.payload = payload;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void close() {

    }

}
