package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

public class OkHttp3UnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<Request.Builder> {
    private OkHttpClient httpClient;

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    protected void addHeaderToUnderlying(Request.Builder underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(Request.Builder underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.header(headerName, headerValue);
    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        HttpMethod method = request.getMethod();
        String rawContentType = request.getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteableMethod(method)) {
            if (request.getPayload() != null) {
                // 压缩处理：
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
                if (!Objs.isEmpty(contentEncodings)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(request.getPayload().size() / 3);
                    OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                    out.write(request.getPayload().toByteArray());
                    out.flush();
                    body = RequestBody.create(contentType, baos.toByteArray());
                    out.close();
                } else {
                    body = RequestBody.create(contentType, request.getPayload().toByteArray());
                }
            } else {
                body = RequestBody.create(contentType, Emptys.EMPTY_BYTES);
            }
        }

        Request.Builder builder = new Request.Builder().url(request.getUri().toURL()).method(method.name(), body);
        writeHeaders(request, builder);
        Request underlyingRequest = builder.build();
        return new OkHttp3UnderlyingHttpResponse(method, request.getUri(), this.httpClient.newCall(underlyingRequest).execute());
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, HttpRequestPayloadWriter payloadWriter) throws Exception {
        HttpMethod method = request.getMethod();
        String rawContentType = request.getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteableMethod(method)) {
            if (request.getPayload() != null) {
                body = new HttpRequestAttachmentPayload(request, payloadWriter, contentType);
            } else {
                body = RequestBody.create(contentType, Emptys.EMPTY_BYTES);
            }
        }

        Request.Builder builder = new Request.Builder().url(request.getUri().toURL()).method(method.name(), body);
        writeHeaders(request, builder);
        Request underlyingRequest = builder.build();
        return new OkHttp3UnderlyingHttpResponse(method, request.getUri(), this.httpClient.newCall(underlyingRequest).execute());
    }
}
