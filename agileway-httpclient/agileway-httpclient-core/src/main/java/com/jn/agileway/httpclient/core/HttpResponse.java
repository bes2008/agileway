package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 代表了http响应，框架内部使用，用户不要直接使用
 */
public interface HttpResponse extends Closeable {
    int getStatusCode();

    void close() throws IOException;

    InputStream getBody() throws IOException;

    HttpHeaders getHeaders();
}
