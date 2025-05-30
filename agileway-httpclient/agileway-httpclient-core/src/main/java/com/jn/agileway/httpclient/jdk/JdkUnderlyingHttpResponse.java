package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.BaseHttpMessage;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

class JdkUnderlyingHttpResponse extends BaseHttpMessage<InputStream> implements UnderlyingHttpResponse {

    private HttpURLConnection httpConnection;

    JdkUnderlyingHttpResponse(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
        this.uri = URI.create(httpConnection.getURL().toString());
        this.method = HttpMethod.resolve(httpConnection.getRequestMethod());
    }

    @Override
    public int getStatusCode() {
        try {
            return this.httpConnection.getResponseCode();
        } catch (IOException ex) {
            return -1;
        }
    }

    @Override
    public void close() {
        this.httpConnection.disconnect();
    }

    @Override
    public InputStream getPayload() {
        try {
            if (this.payload == null) {
                InputStream inputStream = this.httpConnection.getErrorStream();
                if (inputStream == null) {
                    inputStream = this.httpConnection.getInputStream();
                }

                // 处理压缩
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(this.getHttpHeaders());
                inputStream = HttpClientUtils.wrapByContentEncodings(inputStream, contentEncodings);
                this.payload = inputStream;
            }
            return this.payload;
        } catch (IOException ex) {
            throw Throwables.wrapAsRuntimeIOException(ex);
        }
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = super.getHttpHeaders();
        if (headers == null) {
            // Header field 0 is the status line for most HttpURLConnections, but not on GAE
            String name = this.httpConnection.getHeaderFieldKey(0);
            if (Strings.isNotBlank(name)) {
                headers.add(name, this.httpConnection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.httpConnection.getHeaderFieldKey(i);
                if (Strings.isBlank(name)) {
                    break;
                }
                headers.add(name, this.httpConnection.getHeaderField(i));
                i++;
            }
            this.headers.setProtocolHeaders(headers);
        }
        return headers;
    }
}
