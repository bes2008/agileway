package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
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

class OkHttp3HttpRequest extends AbstractUnderlyingHttpRequest {
    private URI uri;
    private HttpMethod method;
    private OkHttpClient httpClient;
    private ByteArrayOutputStream content = new ByteArrayOutputStream(1024);


    OkHttp3HttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, OkHttpClient client) {
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
        return content;
    }

    @Override
    protected void addHeaderToUnderlying(String headerName, String headerValue) {

    }

    @Override
    protected void setHeaderToUnderlying(String headerName, String headerValue) {

    }

    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = (content.size() > 0 ||
                okhttp3.internal.http.HttpMethod.requiresRequestBody(method.name()) ?
                RequestBody.create(contentType, content.toByteArray()) : null);

        Request.Builder builder = new Request.Builder().url(uri.toURL()).method(method.name(), body);
        getHeaders().forEach((headerName, headerValues) -> {
            for (String headerValue : headerValues) {
                builder.addHeader(headerName, headerValue);
            }
        });
        Request request = builder.build();
        return new OkHttp3HttpResponse(method, uri, this.httpClient.newCall(request).execute());
    }
}
