package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.mime.MediaType;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestBodyWriter<T> {
    boolean canWrite(T body, MediaType contentType);

    void write(T body, MediaType contentType, UnderlyingHttpRequest request);
}
