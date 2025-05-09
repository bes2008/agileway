package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpHeaders;

import java.io.Closeable;
import java.io.IOException;

/**
 * 代表了Http响应，它是提供给用户直接使用的
 *
 * @param <T>
 */
public class HttpResponse<T> implements Closeable {
    private UnderlyingHttpResponse response;
    private T body;

    public HttpResponse(UnderlyingHttpResponse response) {
        this(response, null);
    }

    public HttpResponse(UnderlyingHttpResponse response, T body) {
        this.response = response;
        if (body != null) {
            this.body = body;
        } else {
            try {
                this.body = (T) this.response.getBody();
            } catch (IOException e) {
                throw Throwables.wrapAsRuntimeIOException(e);
            }
        }
    }

    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public T getBody() {
        return body;
    }

    @Override
    public void close() throws IOException {
        response.close();
    }
}
