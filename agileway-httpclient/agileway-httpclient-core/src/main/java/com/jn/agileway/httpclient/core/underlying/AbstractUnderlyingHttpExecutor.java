package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.Collection;

public abstract class AbstractUnderlyingHttpExecutor<UnderlyingRequest> implements UnderlyingHttpExecutor<UnderlyingRequest> {

    protected final void completeHeaders(HttpRequest<?> request, UnderlyingRequest underlyingRequest) {
        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHttpHeaders();
        if (HttpClientUtils.requestBodyUseStreamMode(method, headers)) {
            request.getHttpHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        }
        if (!HttpClientUtils.isWriteableMethod(method)) {
            request.getHttpHeaders().remove(HttpHeaders.CONTENT_TYPE);
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
                setHeaderToUnderlying(underlyingRequest, headerName, headerValue);
            } else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    addHeaderToUnderlying(underlyingRequest, headerName, actualHeaderValue);
                }
            }
        }
    }

    protected abstract void addHeaderToUnderlying(UnderlyingRequest underlyingRequest, String headerName, String headerValue);

    protected abstract void setHeaderToUnderlying(UnderlyingRequest underlyingRequest, String headerName, String headerValue);

}
