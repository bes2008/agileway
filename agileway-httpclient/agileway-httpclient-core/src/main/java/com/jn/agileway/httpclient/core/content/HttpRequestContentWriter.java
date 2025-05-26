package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;


/**
 * 用于对请求体内容进行转换
 */
public interface HttpRequestContentWriter<T> {
    boolean canWrite(HttpRequest request);

    void write(HttpRequest request, UnderlyingHttpRequest output) throws IOException;
}
