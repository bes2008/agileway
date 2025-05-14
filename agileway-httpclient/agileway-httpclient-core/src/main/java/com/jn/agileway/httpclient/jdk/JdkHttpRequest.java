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

class JdkHttpRequest extends AbstractUnderlyingHttpRequest {
    private URI uri;
    private HttpMethod method;
    private HttpURLConnection httpConnection;

    private ByteArrayOutputStream bufferedBody;
    private OutputStream streamBody;
    private boolean streamMode;

    JdkHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, HttpURLConnection httpConnection, boolean streamMode) {
        this.streamMode = streamMode;
        this.httpConnection = httpConnection;
        this.uri = uri;
        this.method = method;
        addHeaders(httpHeaders);
        if (!streamMode) {
            this.bufferedBody = new ByteArrayOutputStream(1024);
        }
    }

    @Override
    public OutputStream getContent() throws IOException {
        if (!streamMode) {
            return this.bufferedBody;
        }
        if (streamBody == null) {
            long contentLength = this.getHeaders().getContentLength();
            if (contentLength > 0) {
                this.httpConnection.setFixedLengthStreamingMode(contentLength);
            } else {
                this.httpConnection.setChunkedStreamingMode(4096);
            }
            this.httpConnection.connect();
            writeHeaders();
            OutputStream outputStream = this.httpConnection.getOutputStream();
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncoding(this.getHeaders());
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
            writeHeaders();
            this.httpConnection.connect();
            if (this.httpConnection.getDoOutput()) {
                OutputStream outputStream = this.httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncoding(this.getHeaders());
                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(this.bufferedBody.toByteArray());
                outputStream.flush();
            }
        }
        this.httpConnection.getResponseCode();
        return new JdkHttpResponse(this.httpConnection);
    }

    @Override
    protected void setHeaderToUnderlying(String headerName, String headerValue) {
        httpConnection.setRequestProperty(headerName, headerValue);
    }

    @Override
    protected void addHeaderToUnderlying(String headerName, String headerValue) {
        httpConnection.addRequestProperty(headerName, headerValue);
    }
}
