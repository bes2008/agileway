package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

class JdkUnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpURLConnection> {

    private HttpURLConnection httpConnection;

    private boolean streamMode;

    JdkUnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, HttpURLConnection httpConnection, boolean streamMode) {
        super(method, uri, httpHeaders);
        this.streamMode = streamMode;
        this.httpConnection = httpConnection;
    }

    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        if (!this.streamMode) {
            // buffered 模式
            writeHeaders(this.httpConnection);
            this.httpConnection.connect();
            if (this.httpConnection.getDoOutput() && this.getPayload() != null) {
                OutputStream outputStream = this.httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(this.getHttpHeaders());
                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(this.getPayload().toByteArray());
                outputStream.flush();
            }
        } else {
            try {
                long contentLength = this.getHttpHeaders().getContentLength();
                if (contentLength > 0) {
                    // 要求 提前告知长度
                    this.httpConnection.setFixedLengthStreamingMode(contentLength);
                } else {
                    this.httpConnection.setChunkedStreamingMode(4096);
                }
                writeHeaders(this.httpConnection);
                this.httpConnection.connect();
                OutputStream outputStream = this.httpConnection.getOutputStream();
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(this.getHttpHeaders());
                outputStream = HttpClientUtils.wrapByContentEncodings(outputStream, contentEncodings);
                outputStream.write(this.getPayload().toByteArray());
                outputStream.flush();
            } catch (IOException ex) {
                throw Throwables.wrapAsRuntimeIOException(ex);
            }
        }
        this.httpConnection.getResponseCode();
        return new JdkUnderlyingHttpResponse(this.httpConnection);
    }


    @Override
    protected void setHeaderToUnderlying(HttpURLConnection target, String headerName, String headerValue) {
        target.setRequestProperty(headerName, headerValue);
    }

    @Override
    protected void addHeaderToUnderlying(HttpURLConnection target, String headerName, String headerValue) {
        target.addRequestProperty(headerName, headerValue);
    }
}
