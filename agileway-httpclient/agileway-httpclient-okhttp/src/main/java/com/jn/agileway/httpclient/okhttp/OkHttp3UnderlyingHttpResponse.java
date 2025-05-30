package com.jn.agileway.httpclient.okhttp;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

class OkHttp3UnderlyingHttpResponse implements UnderlyingHttpResponse {
    private URI uri;
    private HttpMethod method;
    private HttpHeaders headers;
    private Response response;

    OkHttp3UnderlyingHttpResponse(HttpMethod method, URI uri, Response response) {
        this.uri = uri;
        this.method = method;
        this.response = response;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public int getStatusCode() {
        return response.code();
    }

    @Override
    public void close() {
        ResponseBody body = this.response.body();
        if (body != null) {
            body.close();
        }
    }

    @Override
    public InputStream getPayload() {
        ResponseBody body = this.response.body();
        return body != null ? body.byteStream() : null;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (headers == null) {
            HttpHeaders headers = new HttpHeaders();
            for (String headerName : this.response.headers().names()) {
                for (String headerValue : this.response.headers(headerName)) {
                    headers.add(headerName, headerValue);
                }
            }
            this.headers = headers;
        }
        return headers;
    }
}
