package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestPayloadWriter<T> {
    boolean canWrite(HttpRequest request);

    void write(HttpRequest request, UnderlyingHttpRequest output) throws Exception;
}
