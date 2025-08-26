package com.jn.agileway.httpclient.hc5;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.timing.TimeDuration;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.compat.ClassicToAsyncAdaptor;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.util.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

class ApacheUnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpUriRequestBase> {
    private CloseableHttpAsyncClient httpClient;
    private TimeDuration requestTimeout;

    ApacheUnderlyingHttpExecutor(CloseableHttpAsyncClient httpClient, TimeDuration timeout) {
        this.httpClient = httpClient;
        this.requestTimeout = timeout;
    }
    @Override
    protected void addHeaderToUnderlying(HttpUriRequestBase underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.addHeader(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpUriRequestBase underlyingRequest, String headerName, String headerValue) {
        underlyingRequest.setHeader(headerName, headerValue);
    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception {
        HttpUriRequestBase underlyingRequest = new HttpUriRequestBase(request.getMethod().name(), request.getUri());
        completeHeaders(request, underlyingRequest);

        HttpEntity contentEntity = null;
        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {
            String contentEncoding = request.getHttpHeaders().getFirst("Content-Encoding");
            if (Strings.isBlank(contentEncoding)) {
                MediaType contentType = request.getHttpHeaders().getContentType();
                contentEntity = HttpEntities.create(request.getPayload().toByteArray(), ContentType.parse(contentType.toString()));
            } else {
                contentEntity = new CompressedBufferedHttpEntity(request.getHttpHeaders().getContentType().toString(), contentEncoding);
                request.getPayload().writeTo((OutputStream) contentEntity);
            }
        }
        underlyingRequest.setEntity(contentEntity);

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.requestTimeout;
            if (requestTimeout == null) {
                requestTimeout = TimeDuration.ofMinutes(2);
            }
        }

        ClassicToAsyncAdaptor classicHttpClient = new ClassicToAsyncAdaptor(this.httpClient, Timeout.ofMilliseconds(requestTimeout.toMillis()));
        CloseableHttpResponse response = classicHttpClient.execute(underlyingRequest);
        return new ApacheUnderlyingHttpResponse(request.getMethod(), request.getUri(), response);
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, HttpRequestPayloadWriter payloadWriter) throws Exception {
        HttpUriRequestBase underlyingRequest = new HttpUriRequestBase(request.getMethod().name(), request.getUri());

        completeHeaders(request, underlyingRequest);
        HttpEntity contentEntity = null;
        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {
            contentEntity = new HttpRequestAttachmentHttpEntity(request, payloadWriter, request.getHttpHeaders().getContentType().toString());
        }

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.requestTimeout;
            if (requestTimeout == null) {
                requestTimeout = TimeDuration.ofMinutes(2);
            }
        }
        underlyingRequest.setEntity(contentEntity);
        ClassicToAsyncAdaptor classicHttpClient = new ClassicToAsyncAdaptor(this.httpClient, Timeout.ofMilliseconds(requestTimeout.toMillis()));
        CloseableHttpResponse response = classicHttpClient.execute(underlyingRequest);
        return new ApacheUnderlyingHttpResponse(request.getMethod(), request.getUri(), response);
    }
}
