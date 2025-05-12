package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestContentWriter<T> {
    boolean canWrite(T body, MediaType contentType);

    void write(T body, MediaType contentType, UnderlyingHttpRequest output) throws IOException;
}
