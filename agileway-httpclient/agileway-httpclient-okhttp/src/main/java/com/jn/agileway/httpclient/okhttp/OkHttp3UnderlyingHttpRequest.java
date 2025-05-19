package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
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
    private URI uri;
    private HttpMethod method;
    private OkHttpClient httpClient;
    private ByteArrayOutputStream content = new ByteArrayOutputStream(1024);


    OkHttp3UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, OkHttpClient client) {
        this.httpClient = client;
        this.uri = uri;
        this.method = method;
        addHeaders(httpHeaders);
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
    public OutputStream getContent() throws IOException {
        if (content == null) {
            if (HttpClientUtils.isWriteable(method)) {
                content = new ByteArrayOutputStream();
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
        if (content.size() > 0 && HttpClientUtils.isWriteable(method)) {
            body = RequestBody.create(contentType, content.toByteArray());
        }
        Request.Builder builder = new Request.Builder().url(uri.toURL()).method(method.name(), body);
        writeHeaders(builder);
        Request request = builder.build();
        return new OkHttp3UnderlyingHttpResponse(method, uri, this.httpClient.newCall(request).execute());
    }
}
