package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.underlying.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.ContentEncoding;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

class Jdk11UnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpRequest.Builder> {
    private HttpClient httpClient;
    private ByteArrayOutputStream bufferedContent;
    private Duration timeout;

    Jdk11UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders headers, HttpClient httpClient, Duration timeout) {
        super(method, uri, headers);
        this.timeout = timeout;
        this.httpClient = httpClient;
    }

    @Override
    public OutputStream getPayload() {
        if (bufferedContent == null) {
            if (HttpClientUtils.isWriteable(getMethod())) {
                bufferedContent = new ByteArrayOutputStream(1024);
            }
        }
        return bufferedContent;
    }

    @Override
    protected void addHeaderToUnderlying(HttpRequest.Builder target, String headerName, String headerValue) {
        if (!Strings.equals("Content-Length", headerName, true)) {
            target.header(headerName, headerValue);
        }
    }

    @Override
    protected void setHeaderToUnderlying(HttpRequest.Builder target, String headerName, String headerValue) {
        if (!Strings.equals("Content-Length", headerName, true)) {
            target.setHeader(headerName, headerValue);
        }
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
        HttpRequest.Builder builder = HttpRequest.newBuilder(getUri());
        writeHeaders(builder);
        if (HttpClientUtils.isWriteable(getMethod())) {
            if (computeContentLength() > 0) {
                // 压缩处理：
                List<ContentEncoding> contentEncodings = HttpClientUtils.getContentEncodings(getHttpHeaders());
                if (!Objs.isEmpty(contentEncodings)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int) computeContentLength() / 5);
                    OutputStream out = HttpClientUtils.wrapByContentEncodings(baos, contentEncodings);
                    out.write(bufferedContent.toByteArray());
                    out.flush();
                    builder.method(getMethod().name(), HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()));
                    out.close();
                } else {
                    builder.method(getMethod().name(), HttpRequest.BodyPublishers.ofByteArray(bufferedContent.toByteArray()));
                }
            } else {
                builder.method(getMethod().name(), HttpRequest.BodyPublishers.noBody());
            }
        } else {
            builder.method(getMethod().name(), HttpRequest.BodyPublishers.noBody());
        }
        builder.timeout(timeout == null ? Duration.ofSeconds(60) : timeout);
        HttpRequest request = builder.build();

        try {
            HttpResponse<byte[]> underlyingHttpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            java.net.http.HttpHeaders headers = underlyingHttpResponse.headers();
            HttpHeaders responseHeaders = new HttpHeaders();
            for (String name : headers.map().keySet()) {
                for (String value : headers.allValues(name)) {
                    responseHeaders.add(name, value);
                }
            }
            Jdk11UnderlyingHttpResponse response = new Jdk11UnderlyingHttpResponse(getMethod(), getUri(), responseHeaders, underlyingHttpResponse.statusCode(), new ByteArrayInputStream(underlyingHttpResponse.body()));
            return response;
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        }

    }
}
