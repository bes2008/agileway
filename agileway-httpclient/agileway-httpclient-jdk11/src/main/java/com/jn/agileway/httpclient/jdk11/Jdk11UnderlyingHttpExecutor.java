package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.timing.TimeDuration;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;

public class Jdk11UnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpRequest.Builder> {
    private HttpClient httpClient;
    private TimeDuration timeout = TimeDuration.ofMinutes(2);

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setTimeout(TimeDuration timeout) {
        this.timeout = timeout;
    }

    @Override
    protected void addHeaderToUnderlying(HttpRequest.Builder underlyingRequest, String headerName, String headerValue) {
        if (!Strings.equals("Content-Length", headerName, true)) {
            underlyingRequest.header(headerName, headerValue);
        }
    }

    @Override
    protected void setHeaderToUnderlying(HttpRequest.Builder underlyingRequest, String headerName, String headerValue) {
        if (!Strings.equals("Content-Length", headerName, true)) {
            underlyingRequest.setHeader(headerName, headerValue);
        }
    }

    @Override
    public UnderlyingHttpResponse executeAttachmentUploadRequest(com.jn.agileway.httpclient.core.HttpRequest<?> request, final HttpRequestPayloadWriter payloadWriter) throws Exception {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        completeHeaders(request, builder);
        Thread writePayload = null;
        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {

            PipedInputStream pipedInputStream = new PipedInputStream();
            PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());

            OutputStream out = HttpClientUtils.wrapByContentEncodings(pipedOutputStream, contentEncodings);
            writePayload = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        payloadWriter.write(request, out);
                    } catch (Exception e) {
                        Loggers.getLogger(Jdk11UnderlyingHttpExecutor.class).error("file upload failed: {}", e.getMessage(), e);
                    } finally {
                        IOs.close(pipedOutputStream);
                    }
                }
            });
            builder.method(method.name(), HttpRequest.BodyPublishers.ofInputStream(() -> pipedInputStream));

        } else {
            builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
        }

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        builder.timeout(Duration.ofMillis(requestTimeout.toMillis()));
        HttpRequest underlyingRequest = builder.build();

        java.net.http.HttpResponse<byte[]> underlyingHttpResponse = null;
        try {
            if (writePayload != null) {
                writePayload.start();
            }
            underlyingHttpResponse = this.httpClient.send(underlyingRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray());
            underlyingHttpResponse.statusCode();
            java.net.http.HttpHeaders headers = underlyingHttpResponse.headers();
            HttpHeaders responseHeaders = new HttpHeaders();
            for (String name : headers.map().keySet()) {
                for (String value : headers.allValues(name)) {
                    responseHeaders.add(name, value);
                }
            }
            Jdk11UnderlyingHttpResponse response = new Jdk11UnderlyingHttpResponse(request.getMethod(), request.getUri(), responseHeaders, underlyingHttpResponse.statusCode(), new ByteArrayInputStream(underlyingHttpResponse.body()));
            return response;
        } catch (InterruptedException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }

    }

    @Override
    public UnderlyingHttpResponse executeBufferedRequest(com.jn.agileway.httpclient.core.HttpRequest<ByteArrayOutputStream> request) throws Exception {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        completeHeaders(request, builder);
        if (HttpClientUtils.isSupportContentMethod(request.getMethod()) && request.getPayload() != null) {
            // 压缩处理：
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            if (!Objs.isEmpty(contentEncodings)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(request.getPayload().size() / 3);
                OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                out.write(request.getPayload().toByteArray());
                out.flush();
                builder.method(method.name(), HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()));
                out.close();
            } else {
                builder.method(method.name(), HttpRequest.BodyPublishers.ofByteArray(request.getPayload().toByteArray()));
            }
        } else {
            builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
        }

        TimeDuration requestTimeout = request.getHeaders().get(MessageHeaderConstants.REQUEST_KEY_TIMEOUT, TimeDuration.class);
        if (requestTimeout == null) {
            requestTimeout = this.timeout;
        }
        builder.timeout(Duration.ofMillis(requestTimeout.toMillis()));
        HttpRequest underlyingRequest = builder.build();

        java.net.http.HttpResponse<byte[]> underlyingHttpResponse = null;
        try {
            underlyingHttpResponse = this.httpClient.send(underlyingRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray());
            underlyingHttpResponse.statusCode();
            java.net.http.HttpHeaders headers = underlyingHttpResponse.headers();
            HttpHeaders responseHeaders = new HttpHeaders();
            for (String name : headers.map().keySet()) {
                for (String value : headers.allValues(name)) {
                    responseHeaders.add(name, value);
                }
            }
            Jdk11UnderlyingHttpResponse response = new Jdk11UnderlyingHttpResponse(request.getMethod(), request.getUri(), responseHeaders, underlyingHttpResponse.statusCode(), new ByteArrayInputStream(underlyingHttpResponse.body()));
            return response;
        } catch (InterruptedException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }

    }

}
