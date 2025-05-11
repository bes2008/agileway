package com.jn.agileway.httpclient.jodd;

import com.jn.agileway.httpclient.core.AbstractUnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.net.http.HttpMethod;
import jodd.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

class JoddHttpRequest extends AbstractUnderlyingHttpRequest {
    private HttpRequest joddRequest;

    private ByteArrayOutputStream bufferedBody;
    private OutputStream streamBody;
    private boolean streamMode;

    JoddHttpRequest(HttpRequest joddRequest, boolean streamMode) {
        this.joddRequest = joddRequest;
        this.streamMode = streamMode;
        if (!streamMode) {
            this.bufferedBody = new ByteArrayOutputStream();
        }
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(joddRequest.method());
    }

    @Override
    public URI getUri() {
        String uriString = joddRequest.url();
        try {
            return new URI(uriString);
        } catch (Exception ex) {
            throw new IllegalStateException(StringTemplates.formatWithPlaceholder("invalid URI: {}", uriString));
        }
    }

    @Override
    public OutputStream getBody() throws IOException {
        if (!streamMode) {
            return bufferedBody;
        }
        if (streamBody == null) {
            joddRequest.open();
        }
        return streamBody;
    }


    @Override
    protected UnderlyingHttpResponse exchangeInternal() throws IOException {
        return null;
    }
}
