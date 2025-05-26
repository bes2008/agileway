package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpMessage;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 代表一个http请求
 * <p>
 * 框架底层使用，用户不直接使用
 */
public interface UnderlyingHttpRequest extends HttpMessage<OutputStream> {
    /**
     * 获取body输出流，用于输出数据
     *
     * @return body输出流
     * @throws IOException 出错
     */
    @Override
    OutputStream getContent();

    UnderlyingHttpResponse exchange() throws IOException;

}
