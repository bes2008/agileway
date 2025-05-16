package com.jn.agileway.httpclient.jdk11;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.struct.Holder;

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

    Jdk11UnderlyingHttpRequest(HttpMethod method, URI uri, HttpClient httpClient, Duration timeout) {
        this.method = method;
        this.uri = uri;
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
    protected void addHeaderToUnderlying(HttpRequest.Builder context, String headerName, String headerValue) {
        context.header(headerName, headerValue);
    }

    @Override
    protected void setHeaderToUnderlying(HttpRequest.Builder context, String headerName, String headerValue) {
        context.setHeader(headerName, headerValue);
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
        Jdk11UnderlyingHttpResponse response = new Jdk11UnderlyingHttpResponse(method, uri);
        Holder<HttpResponse.BodySubscriber<byte[]>> contentHolder = new Holder<HttpResponse.BodySubscriber<byte[]>>();
        try {
            httpClient.send(request, new HttpResponse.BodyHandler<byte[]>() {
                @Override
                public HttpResponse.BodySubscriber<byte[]> apply(HttpResponse.ResponseInfo responseInfo) {
                    response.statusCode = responseInfo.statusCode();
                    java.net.http.HttpHeaders headers = responseInfo.headers();
                    for (String name : headers.map().keySet()) {
                        for (String value : headers.allValues(name)) {
                            response.headers.add(name, value);
                        }
                    }
                    HttpResponse.BodySubscriber<byte[]> bodySubscriber = HttpResponse.BodyHandlers.ofByteArray().apply(responseInfo);
                    contentHolder.set(bodySubscriber);
                    return bodySubscriber;
                }
            });

            if (contentHolder.get() != null) {
                byte[] content = contentHolder.get().getBody().<byte[]>toCompletableFuture().get();
                response.content = new ByteArrayInputStream(content);
            }
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
        return response;
    }
}
