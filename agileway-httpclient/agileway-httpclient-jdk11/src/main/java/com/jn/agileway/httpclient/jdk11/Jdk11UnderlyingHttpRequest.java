package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
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

class Jdk11UnderlyingHttpRequest extends AbstractUnderlyingHttpRequest<HttpRequest.Builder> {
    private HttpClient httpClient;
    private URI uri;
    private HttpMethod method;
    private ByteArrayOutputStream content;
    private Duration timeout;

    Jdk11UnderlyingHttpRequest(HttpMethod method, URI uri, HttpHeaders headers, HttpClient httpClient, Duration timeout) {
        super(method, uri, headers);
        this.timeout = timeout;
        this.httpClient = httpClient;
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
    protected void addHeaderToUnderlying(HttpRequest.Builder target, String headerName, String headerValue) {
        target.header(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpRequest.Builder target, String headerName, String headerValue) {
        target.setHeader(headerName, headerValue);
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
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        if (content != null) {
            builder.method(method.name(), HttpRequest.BodyPublishers.ofByteArray(content.toByteArray()));
        } else {
            builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
        }
        writeHeaders(builder);
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
            Jdk11UnderlyingHttpResponse response = new Jdk11UnderlyingHttpResponse(method, uri, responseHeaders, underlyingHttpResponse.statusCode(), new ByteArrayInputStream(underlyingHttpResponse.body()));
            return response;
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        }

    }
}
