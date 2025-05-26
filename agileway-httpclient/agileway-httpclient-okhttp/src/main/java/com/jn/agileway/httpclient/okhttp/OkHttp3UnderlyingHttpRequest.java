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
    private ByteArrayOutputStream bufferedContent;


    OkHttp3UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders httpHeaders, OkHttpClient client) {
        super(method, uri, httpHeaders);
        this.httpClient = client;
    }

    @Override
    public OutputStream getContent() {
        if (bufferedContent == null) {
            if (HttpClientUtils.isWriteable(getMethod())) {
                bufferedContent = new ByteArrayOutputStream(1024);
            }
        }
        return bufferedContent;
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
        if (this.bufferedContent == null) {
            return -1L;
        }
        return bufferedContent.size();
    }

    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteable(getMethod())) {
            if (computeContentLength() > 0) {
                // 压缩处理：
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(getHeaders());
                if (!Objs.isEmpty(contentEncodings)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int) computeContentLength() / 5);
                    OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                    out.write(bufferedContent.toByteArray());
                    out.flush();
                    body = RequestBody.create(contentType, baos.toByteArray());
                    out.close();
                } else {
                    body = RequestBody.create(contentType, bufferedContent.toByteArray());
                }
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
