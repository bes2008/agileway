package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 代表一个http请求
 * <p>
 * 框架底层使用，用户不直接使用
 */
public interface UnderlyingHttpRequest extends HttpMessage<ByteArrayOutputStream> {

    void setPayload(ByteArrayOutputStream payload);

    UnderlyingHttpResponse exchange() throws IOException;

}
