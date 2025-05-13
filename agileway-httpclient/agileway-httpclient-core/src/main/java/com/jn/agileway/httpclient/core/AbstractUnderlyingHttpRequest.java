package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

public abstract class AbstractUnderlyingHttpRequest implements UnderlyingHttpRequest {

    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;


    public void addHeaders(HttpHeaders headers) {
        if (!executed) {
            for (String key : headers.keySet()) {
                List<String> values = headers.get(key);
                for (String value : values) {
                    this.headers.addIfValueAbsent(key, value);
                }
            }
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
    public UnderlyingHttpResponse exchange() throws IOException {
        Preconditions.checkState(!executed, "http already executed");
        UnderlyingHttpResponse promise = this.exchangeInternal();
        this.executed = true;
        return promise;
    }

    protected abstract UnderlyingHttpResponse exchangeInternal() throws IOException;
}
