package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.OutputStream;

class ApacheUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpUriRequest> {
    private HttpUriRequest request;
    private CloseableHttpClient underlyingClient;

    ApacheUnderlyingHttpRequest(CloseableHttpClient client, HttpUriRequest request, HttpHeaders httpHeaders) {
        super(HttpMethod.resolve(request.getMethod()), request.getURI(), httpHeaders);
        this.underlyingClient = client;
        this.request = request;
    }


    @Override
    protected void addHeaderToUnderlying(HttpUriRequest target, String headerName, String headerValue) {
        // apache httpclient 不支持由用户写 Content-Length，要由要 它自己来写
        if (Strings.equals("Content-Length", headerName, true)) {
            return;
        }
        target.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpUriRequest target, String headerName, String headerValue) {
        if (Strings.equals("Content-Length", headerName, true)) {
            return;
        }
        target.removeHeaders(headerName);
        target.addHeader(headerName, headerValue);
    }


    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        writeHeaders(this.request);
        HttpEntity contentEntity = null;
        if (HttpClientUtils.isWriteable(this.method) && this.getPayload() != null) {
            String contentEncoding = getHttpHeaders().getFirst("Content-Encoding");
            if (Strings.isBlank(contentEncoding)) {
                contentEntity = new BufferedHttpEntity(getHttpHeaders().getContentType().toString());
            } else {
                contentEntity = new CompressedHttpEntity(getHttpHeaders().getContentType().toString(), contentEncoding);
            }

        }

        if (this.request instanceof HttpEntityEnclosingRequestBase && contentEntity != null) {
            HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) this.request;
            request.setEntity(contentEntity);
            this.getPayload().writeTo((OutputStream) contentEntity);
        }
        CloseableHttpResponse response = this.underlyingClient.execute(request);
        return new ApacheUnderlyingHttpResponse(this.getMethod(), this.getUri(), response);
    }
}
