package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * 代表一个http请求
 * <p>
 * 框架底层使用，用户不直接使用
 */
public interface UnderlyingHttpRequest {
    HttpMethod getMethod();

    URI getUri();

    HttpHeaders getHeaders();

    /**
     * 获取body输出流，用于输出数据
     *
     * @return body输出流
     * @throws IOException 出错
     */
    OutputStream getBufferedContent() throws IOException;

    UnderlyingHttpResponse exchange() throws IOException;

}
