package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralBytesHttpResponseReader implements HttpResponsePayloadReader<byte[]> {
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        return expectedContentType == byte[].class;
    }

    @Override
    public byte[] read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        int contentLength = (int) response.getHttpHeaders().getContentLength();
        if (contentLength < 0) {
            throw new IOException("Content-Length header is required for Content-Type " + contentType);
        }
        return response.getPayload();
    }
}
