package com.jn.agileway.httpclient.httpcomponents.httpexchange;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
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
    private HttpEntity contentEntity;

    ApacheUnderlyingHttpRequest(CloseableHttpClient client, HttpUriRequest request, HttpHeaders httpHeaders) {
        super(HttpMethod.resolve(request.getMethod()), request.getURI(), httpHeaders);
        this.underlyingClient = client;
        this.request = request;
    }


    @Override
    public OutputStream getBufferedContent() throws IOException {
        if (this.contentEntity == null) {
            if (HttpClientUtils.isWriteable(this.getMethod())) {
                String contentEncoding = getHeaders().getFirst("Content-Encoding");
                if (Strings.isBlank(contentEncoding)) {
                    this.contentEntity = new BufferedHttpEntity(getHeaders().getContentType().toString());
                } else {
                    this.contentEntity = new CompressedHttpEntity(getHeaders().getContentType().toString(), contentEncoding);
                }
            }
        }
        return (OutputStream) this.contentEntity;
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
