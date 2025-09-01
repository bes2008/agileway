package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;

import java.io.ByteArrayOutputStream;

public interface UnderlyingHttpExecutor<TARGET> {

    /**
     * 执行一个 request, payload 的内容会先放在 一个 ByteArrayOutputStream 中，再从 ByteArrayOutputStream 中获取内容，再发送到目标服务器
     * <p>
     * 如果要启用 压缩，请在 request header 中设置 Content-Encoding
     */
    UnderlyingHttpResponse executeBufferedRequest(HttpRequest<ByteArrayOutputStream> request) throws Exception;

    /**
     * 执行一个 request, payload 的内容不会预先 序列化，而是 由它的 writer 在这里写
     * <p>
     * 如果要启用 压缩，请在 request header 中设置 Content-Encoding
     */
    UnderlyingHttpResponse executeAttachmentUploadRequest(HttpRequest<?> request, HttpRequestPayloadWriter payloadWriter) throws Exception;

    void executeSseRequest(HttpRequest request) throws Exception;
}
