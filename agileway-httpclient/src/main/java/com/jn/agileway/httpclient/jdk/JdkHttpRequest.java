package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.AbstractHttpRequest;
import com.jn.agileway.httpclient.HttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class JdkHttpRequest extends AbstractHttpRequest {

    private HttpURLConnection httpConnection;

    public JdkHttpRequest(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
    }

    @Override
    protected HttpResponse exchangeInternal() throws IOException {
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(this.httpConnection.getRequestMethod());
    }

    @Override
    public URI getUri() {
        try {
            return this.httpConnection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Error occur when get HttpURLConnection URI: {}", ex.getMessage()), ex);
        }
    }

}
