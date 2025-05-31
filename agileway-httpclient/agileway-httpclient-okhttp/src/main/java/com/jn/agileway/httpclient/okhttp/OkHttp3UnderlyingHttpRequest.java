package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import okhttp3.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;

class OkHttp3UnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<Request.Builder> {
    private OkHttpClient httpClient;

    OkHttp3UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, OkHttpClient client) {
        super(method, uri, httpHeaders);
        this.httpClient = client;
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
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        String rawContentType = getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteable(getMethod()) && getPayload() != null) {
            // 压缩处理：
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(getHttpHeaders());
            if (!Objs.isEmpty(contentEncodings)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(getPayload().size() / 5);
                OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                out.write(getPayload().toByteArray());
                out.flush();
                body = RequestBody.create(contentType, baos.toByteArray());
                out.close();
            } else {
                body = RequestBody.create(contentType, getPayload().toByteArray());
            }
        } else {
            body = RequestBody.create(contentType, Emptys.EMPTY_BYTES);
        }
        Request.Builder builder = new Request.Builder().url(getUri().toURL()).method(getMethod().name(), body);
        writeHeaders(builder);
        Request request = builder.build();
        return new OkHttp3UnderlyingHttpResponse(getMethod(), getUri(), this.httpClient.newCall(request).execute());
    }
}
