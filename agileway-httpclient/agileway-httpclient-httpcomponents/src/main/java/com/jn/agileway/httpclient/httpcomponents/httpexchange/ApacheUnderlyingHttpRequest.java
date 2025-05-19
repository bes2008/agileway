package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
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
import java.net.URI;

class ApacheUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpUriRequest> {
    private HttpUriRequest request;
    private CloseableHttpClient underlyingClient;
    private HttpEntity contentEntity;
    private HttpHeaders httpHeaders;

    ApacheUnderlyingHttpRequest(CloseableHttpClient client, HttpUriRequest request, HttpHeaders httpHeaders) {
        this.underlyingClient = client;
        this.request = request;
        this.httpHeaders = httpHeaders;
        this.addHeaders(httpHeaders);
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(request.getMethod());
    }

    @Override
    public URI getUri() {
        return request.getURI();
    }

    @Override
    public OutputStream getContent() throws IOException {
        if (this.contentEntity == null) {
            String contentEncoding = this.httpHeaders.getFirst("Content-Encoding");
            if (Strings.isBlank(contentEncoding)) {
                this.contentEntity = new BufferedHttpEntity(this.httpHeaders.getContentType().toString());
            } else {
                this.contentEntity = new CompressedHttpEntity(this.httpHeaders.getContentType().toString(), contentEncoding);
            }
        }
        return (OutputStream) this.contentEntity;
    }

    @Override
    protected void addHeaderToUnderlying(HttpUriRequest context, String headerName, String headerValue) {
        context.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpUriRequest context, String headerName, String headerValue) {
        context.removeHeaders(headerName);
        context.addHeader(headerName, headerValue);
    }


    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        writeHeaders(this.request);
        if (this.request instanceof HttpEntityEnclosingRequestBase && this.contentEntity != null) {
            HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) this.request;
            request.setEntity(this.contentEntity);
        }
        CloseableHttpResponse response = this.underlyingClient.execute(request);
        return new ApacheUnderlyingHttpResponse(this.getMethod(), this.getUri(), response);
    }

    @Override
    protected long computeContentLength() {
        if (this.contentEntity == null) {
            return -1L;
        }
        if (this.contentEntity instanceof BufferedHttpEntity) {
            return ((BufferedHttpEntity) this.contentEntity).getContentLength();
        }
        if (this.contentEntity instanceof CompressedHttpEntity) {
            return ((CompressedHttpEntity) this.contentEntity).getContentLength();
        }
        return -1L;
    }
}
