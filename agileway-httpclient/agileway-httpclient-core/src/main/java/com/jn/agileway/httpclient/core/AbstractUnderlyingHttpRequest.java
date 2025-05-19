package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class AbstractUnderlyingHttpRequest<TARGET> implements UnderlyingHttpRequest {

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

    protected final void writeHeaders(TARGET target) {
        HttpMethod method = getMethod();
        if (HttpClientUtils.requestBodyUseStreamMode(method, headers)) {
            getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        }
        if (method == HttpMethod.GET || method.equals(HttpMethod.HEAD) || method.equals(HttpMethod.OPTIONS) || method.equals(HttpMethod.TRACE)) {
            getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        } else {
            long contentLength = computeContentLength();
            if (contentLength >= 0) {
                getHeaders().setContentLength(contentLength);
            } else {
                getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
            }
        }
        if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)) {
            if (Strings.isBlank(headers.getFirst(HttpHeaders.ACCEPT))) {
                // Avoid "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"
                // from HttpUrlConnection which prevents JSON error response details.
                headers.set(HttpHeaders.ACCEPT, "*/*");
            }
        }

        for (String headerName : headers.keySet()) {
            Collection<String> headerValues = headers.get(headerName);
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {  // RFC 6265
                String headerValue = Strings.join("; ", headerValues);
                setHeaderToUnderlying(target, headerName, headerValue);
            } else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    addHeaderToUnderlying(target,
                            headerName, actualHeaderValue);
                }
            }
        }
    }

    /**
     * 会在写 header之前调用，当有 Content-Encoding 时，会返回 -1
     */
    protected abstract long computeContentLength();

    protected abstract void addHeaderToUnderlying(TARGET target, String headerName, String headerValue);

    protected abstract void setHeaderToUnderlying(TARGET target, String headerName, String headerValue);

    protected abstract UnderlyingHttpResponse exchangeInternal() throws IOException;
}
