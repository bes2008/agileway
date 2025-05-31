package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpExecutor;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;

public class Jdk11UnderlyingHttpExecutor extends AbstractUnderlyingHttpExecutor<HttpRequest.Builder> {
    private HttpClient httpClient;
    private Duration timeout = Duration.ofMinutes(2);

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setTimeout(Duration timeout) {
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
    public HttpResponse<InputStream> execute(com.jn.agileway.httpclient.core.HttpRequest<ByteArrayOutputStream> request) throws Exception {
        return exchangeInternal(request);
    }

    protected HttpResponse<InputStream> exchangeInternal(com.jn.agileway.httpclient.core.HttpRequest<ByteArrayOutputStream> request) throws IOException {
        HttpMethod method = request.getMethod();
        URI uri = request.getUri();
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        writeHeaders(request, builder);
        if (HttpClientUtils.isWriteable(request.getMethod()) && request.getPayload() != null) {
            // 压缩处理：
            List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(request.getHttpHeaders());
            if (!Objs.isEmpty(contentEncodings)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(request.getPayload().size() / 5);
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
        builder.timeout(timeout == null ? Duration.ofSeconds(60) : timeout);
        HttpRequest underlyingRequest = builder.build();

        java.net.http.HttpResponse<byte[]> underlyingHttpResponse = null;
        try {
            underlyingHttpResponse = this.httpClient.send(underlyingRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray());
            int statusCode = underlyingHttpResponse.statusCode();
            java.net.http.HttpHeaders headers = underlyingHttpResponse.headers();
            HttpHeaders responseHeaders = new HttpHeaders();
            for (String name : headers.map().keySet()) {
                for (String value : headers.allValues(name)) {
                    responseHeaders.add(name, value);
                }
            }
            HttpResponse<InputStream> response = new HttpResponse(
                    method,
                    uri,
                    statusCode,
                    responseHeaders,
                    null,
                    new ByteArrayInputStream(underlyingHttpResponse.body()));
            return response;
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        } finally {
            IOs.close(underlyingHttpResponse);
        }

    }

}
