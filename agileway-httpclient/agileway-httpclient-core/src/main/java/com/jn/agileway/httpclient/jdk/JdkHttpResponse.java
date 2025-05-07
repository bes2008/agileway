package com.jn.agileway.httpclient.jdk;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class JdkHttpResponse implements HttpResponse {

    private HttpURLConnection httpConnection;

    JdkHttpResponse(HttpURLConnection httpConnection) {
        this.httpConnection = httpConnection;
    }

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public InputStream getBody() throws IOException {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }
}
