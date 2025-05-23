package com.jn.agileway.httpclient.core.underlying;

import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * 代表了http响应，框架内部使用，用户不要直接使用
 */
public interface UnderlyingHttpResponse extends Closeable {
    URI getUri();

    HttpMethod getMethod();

    int getStatusCode();

    void close();

    /**
     * 获取响应体
     *
     * @return 如果没有响应体，可以返回null
     */
    InputStream getContent() throws IOException;

    HttpHeaders getHeaders();
}
