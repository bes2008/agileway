package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.IOException;

public abstract class AbstractHttpRequest implements HttpRequest {

    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    private byte[] body;

    public void setBody(byte[] body) {
        this.body = body;
    }

    protected byte[] getBody() throws IOException {
        return body;
    }

    public void setHeaders(HttpHeaders headers) {
        if (!executed) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
    }
    @Override
    public final HttpHeaders getHeaders() {
        if (executed) {
            return new HttpHeaders(this.headers);
        }
        return this.headers;
    }

    @Override
    public HttpResponse exchange() throws IOException {
        Preconditions.checkState(!executed, "http already executed");
        HttpResponse promise = this.exchangeInternal();
        this.executed = true;
        return promise;
    }

    protected abstract HttpResponse exchangeInternal() throws IOException;
}
