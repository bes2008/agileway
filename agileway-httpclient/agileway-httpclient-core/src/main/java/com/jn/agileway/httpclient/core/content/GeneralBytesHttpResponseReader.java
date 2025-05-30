package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralBytesHttpResponseReader implements HttpResponseContentReader<byte[]> {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        return expectedContentType == byte[].class;
    }

    @Override
    public byte[] read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        int contentLength = (int) response.getHeaders().getContentLength();
        if (contentLength < 0) {
            throw new IOException("Content-Length header is required for Content-Type " + contentType);
        }
        byte[] bytes = new byte[contentLength];
        if (contentLength > 0) {
            IOs.read(response.getPayload(), bytes);
        }
        return bytes;
    }
}
