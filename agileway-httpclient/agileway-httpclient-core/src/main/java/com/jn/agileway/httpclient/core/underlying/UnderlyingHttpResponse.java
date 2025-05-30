package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.HttpResponseMessage;

import java.io.Closeable;
import java.io.InputStream;

/**
 * 代表了http响应，框架内部使用，用户不要直接使用
 */
public interface UnderlyingHttpResponse extends HttpResponseMessage<InputStream>, Closeable {

    void close();

    /**
     * 获取响应体
     *
     * @return 如果没有响应体，可以返回null
     */
    @Override
    InputStream getPayload();

}
