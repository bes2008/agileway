package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

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

    protected final void writeHeaders() {
        HttpMethod method = getMethod();
        if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)) {
            if (Strings.isBlank(headers.getFirst(HttpHeaders.ACCEPT))) {
                // Avoid "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"
                // from HttpUrlConnection which prevents JSON error response details.
                headers.set(HttpHeaders.ACCEPT, "*/*");
            }
        }
        headers.forEach((headerName, headerValues) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {  // RFC 6265
                String headerValue = Strings.join("; ", headerValues);
                setHeaderToUnderlying(headerName, headerValue);
            } else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    addHeaderToUnderlying(headerName, actualHeaderValue);
                }
            }
        });
    }

    protected abstract void addHeaderToUnderlying(String headerName, String headerValue);

    protected abstract void setHeaderToUnderlying(String headerName, String headerValue);

    protected abstract UnderlyingHttpResponse exchangeInternal() throws IOException;
}
