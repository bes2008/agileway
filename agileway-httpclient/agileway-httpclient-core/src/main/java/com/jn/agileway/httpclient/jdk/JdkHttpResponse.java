package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class JdkHttpResponse implements UnderlyingHttpResponse {

    private URI uri;
    private HttpMethod method;
    private HttpURLConnection httpConnection;
    /**
     * 响应头
     */
    private HttpHeaders headers;

    private InputStream responseStream;

    JdkHttpResponse(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
        this.uri = URI.create(httpConnection.getURL().toString());
        this.method = HttpMethod.resolve(httpConnection.getRequestMethod());
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
    public InputStream getBody() throws IOException {
        if (this.responseStream == null) {
            InputStream inputStream = this.httpConnection.getErrorStream();
            if (inputStream == null) {
                inputStream = this.httpConnection.getInputStream();
            }
            List<ContentEncoding> contentEncodings = HttpUtils.getContentEncoding(this.getHeaders());
            if (Objs.isNotEmpty(contentEncodings)) {
                for (ContentEncoding contentEncoding : contentEncodings) {
                    if (ContentEncoding.GZIP.equals(contentEncoding)) {
                        inputStream = new GZIPInputStream(inputStream);
                    } else if (ContentEncoding.DEFLATE.equals(contentEncoding)) {
                        inputStream = new InflaterInputStream(inputStream);
                    } else {
                        throw new UnsupportedEncodingException("Unsupported http Content-Encoding: " + contentEncoding);
                    }
                }
            }
        }
        return this.responseStream;
    }

    public void setBody(InputStream responseStream) {
        this.responseStream = responseStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            // Header field 0 is the status line for most HttpURLConnections, but not on GAE
            String name = this.httpConnection.getHeaderFieldKey(0);
            if (Strings.isNotBlank(name)) {
                this.headers.add(name, this.httpConnection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.httpConnection.getHeaderFieldKey(i);
                if (Strings.isBlank(name)) {
                    break;
                }
                this.headers.add(name, this.httpConnection.getHeaderField(i));
                i++;
            }
        }
        return this.headers;
    }
}
