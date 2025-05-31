package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class OkHttp3UnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<Request.Builder> {
    private OkHttpClient httpClient;

    @Override
    protected void addHeaderToUnderlying(Request.Builder underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(Request.Builder underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.header(headerName, headerValue);
    }

    @Override
    public HttpResponse<InputStream> execute(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        return exchangeInternal(request);
    }


    protected HttpResponse<InputStream> exchangeInternal(HttpRequest<ByteArrayOutputStream> request) throws IOException {
        String rawContentType = request.getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        okhttp3.MediaType contentType = Strings.isNotEmpty(rawContentType) ? okhttp3.MediaType.parse(rawContentType) : null;
        RequestBody body = null;
        if (HttpClientUtils.isWriteable(request.getMethod()) && request.getPayload() != null) {
            // 压缩处理：
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            if (!Objs.isEmpty(contentEncodings)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(request.getPayload().size() / 5);
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
        Request.Builder builder = new Request.Builder().url(request.getUri().toURL()).method(request.getMethod().name(), body);
        writeHeaders(request, builder);
        Request underlyingRequest = builder.build();
        Response underlyingResponse = this.httpClient.newCall(underlyingRequest).execute();
        int statusCode = underlyingResponse.code();
        HttpHeaders responseHttpHeaders = getResponseHttpHeaders(underlyingResponse);

        ResponseBody responseBody = underlyingResponse.body();
        InputStream responsePayload = responseBody == null ? null : responseBody.byteStream();
        return new HttpResponse<InputStream>(
                request.getMethod(),
                request.getUri(),
                statusCode,
                responseHttpHeaders,
                underlyingResponse.message(),
                responsePayload);
    }

    private HttpHeaders getResponseHttpHeaders(Response response) {
        HttpHeaders headers = new HttpHeaders();
        for (String headerName : response.headers().names()) {
            for (String headerValue : response.headers(headerName)) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }
}
