package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;

public abstract class AbstractUnderlyingHttpRequest<TARGET> extends BaseHttpMessage<OutputStream> implements UnderlyingHttpRequest {
    private boolean executed = false;

    protected AbstractUnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders headers) {
        this.method = method;
        this.uri = uri;
        addHeaders(headers);
    }

    private void addHeaders(HttpHeaders headers) {
        if (!executed) {
            for (String key : headers.keySet()) {
                List<String> values = headers.get(key);
                for (String value : values) {
                    getHttpHeaders().addIfValueAbsent(key, value);
                }
            }
        }
    }

    @Override
    public final HttpHeaders getHttpHeaders() {
        if (executed) {
            return new HttpHeaders(super.getHttpHeaders());
        }
        return super.getHttpHeaders();
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
        HttpHeaders headers = getHttpHeaders();
        if (HttpClientUtils.requestBodyUseStreamMode(method, headers)) {
            getHttpHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        }
        if (!HttpClientUtils.isWriteable(method)) {
            getHttpHeaders().remove(HttpHeaders.CONTENT_TYPE);
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
                    addHeaderToUnderlying(target, headerName, actualHeaderValue);
                }
            }
        }
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    /**
     * 会在写 header之前调用，当有 Content-Encoding 时，会返回 -1
     */
    protected abstract long computeContentLength();

    protected abstract void addHeaderToUnderlying(TARGET target, String headerName, String headerValue);

    protected abstract void setHeaderToUnderlying(TARGET target, String headerName, String headerValue);

    protected abstract UnderlyingHttpResponse exchangeInternal() throws IOException;
}
