package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import okhttp3.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import okhttp3.Request;
import okhttp3.RequestBody;

class OkHttp3UnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<Request.Builder> {
    private OkHttpClient httpClient;
    private ByteArrayOutputStream content;


    OkHttp3UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, OkHttpClient client) {
        super(method, uri, httpHeaders);
        this.httpClient = client;
    }

    @Override
    public OutputStream getContent() throws IOException {
        if (content == null) {
            if (HttpClientUtils.isWriteable(getMethod())) {
                content = new ByteArrayOutputStream(1024);
            }
        }
        return content;
    }

    @Override
    protected void addHeaderToUnderlying(Request.Builder target, String headerName, String headerValue) {
        target.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(Request.Builder target, String headerName, String headerValue) {
        target.header(headerName, headerValue);
    }

    @Override
    protected long computeContentLength() {
        if (this.content == null) {
            return -1L;
        }
        return content.size();
    }

    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteable(getMethod())) {
            if (computeContentLength() > 0) {
                body = RequestBody.create(contentType, content.toByteArray());
            } else {
                body = RequestBody.create(contentType, Emptys.EMPTY_BYTES);
            }
        }
        Request.Builder builder = new Request.Builder().url(getUri().toURL()).method(getMethod().name(), body);
        writeHeaders(builder);
        Request request = builder.build();
        return new OkHttp3UnderlyingHttpResponse(getMethod(), getUri(), this.httpClient.newCall(request).execute());
    }
}
