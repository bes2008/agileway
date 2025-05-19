package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

class JdkUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpURLConnection> {
    private URI uri;
    private HttpMethod method;
    private HttpURLConnection httpConnection;

    private ByteArrayOutputStream bufferedBody;
    private OutputStream streamBody;
    private boolean streamMode;

    JdkUnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, HttpURLConnection httpConnection, boolean streamMode) {
        this.streamMode = streamMode;
        this.httpConnection = httpConnection;
        this.uri = uri;
        this.method = method;
        addHeaders(httpHeaders);
    }

    @Override
    public OutputStream getContent() throws IOException {
        if (!HttpClientUtils.isWriteable(getMethod())) {
            return null;
        }
        if (!streamMode) {
            if (this.bufferedBody == null) {
                this.bufferedBody = new ByteArrayOutputStream(1024);
            }
            return this.bufferedBody;
        }
        if (streamBody == null) {
            long contentLength = this.getHeaders().getContentLength();
            if (contentLength > 0) {
                // 要求 提前告知长度
                this.httpConnection.setFixedLengthStreamingMode(contentLength);
            } else {
                this.httpConnection.setChunkedStreamingMode(4096);
            }
            writeHeaders(this.httpConnection);
            this.httpConnection.connect();
            OutputStream outputStream = this.httpConnection.getOutputStream();
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(this.getHeaders());
            outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
            this.streamBody = outputStream;
        }

        return streamBody;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        if (!this.streamMode) {
            // buffered 模式
            writeHeaders(this.httpConnection);
            this.httpConnection.connect();
            if (this.httpConnection.getDoOutput()) {
                OutputStream outputStream = this.httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(this.getHeaders());
                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(this.bufferedBody.toByteArray());
                outputStream.flush();
            }
        }
        this.httpConnection.getResponseCode();
        return new JdkUnderlyingHttpResponse(this.httpConnection);
    }

    @Override
    protected long computeContentLength() {
        if (!streamMode) {
            return this.bufferedBody.size();
        }
        return -1L;
    }

    @Override
    protected void setHeaderToUnderlying(HttpURLConnection context, String headerName, String headerValue) {
        context.setRequestProperty(headerName, headerValue);
    }

    @Override
    protected void addHeaderToUnderlying(HttpURLConnection context, String headerName, String headerValue) {
        context.addRequestProperty(headerName, headerValue);
    }
}
