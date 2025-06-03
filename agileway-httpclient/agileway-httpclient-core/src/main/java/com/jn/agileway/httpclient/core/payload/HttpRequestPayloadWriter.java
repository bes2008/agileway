package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;

import java.io.OutputStream;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestPayloadWriter {
    boolean canWrite(HttpRequest<?> request);

    void write(HttpRequest<?> request, OutputStream output) throws Exception;
}
