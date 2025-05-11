package com.jn.agileway.httpclient.core.serialize;

import com.jn.agileway.httpclient.core.HttpResponseBodyReader;
import com.jn.agileway.httpclient.core.UnderlyingHttpResponse;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class GeneralFontHttpResponseReader implements HttpResponseBodyReader<byte[]> {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) {
        if ("font".equals(contentType.getType())) {
            return true;
        } else if ("application".equals(contentType.getType())) {
            return Strings.startsWith(contentType.getSubtype(), "font-") || Strings.contains(contentType.getSubtype(), "-fontobject");
        }
        return false;
    }

    @Override
    public byte[] read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException {
        int contentLength = (int) response.getHeaders().getContentLength();
        if (contentLength < 0) {
            throw new IOException("Content-Length header is required for font");
        }
        byte[] bytes = new byte[contentLength];
        if (contentLength > 0) {
            IOs.read(response.getBody(), bytes);
        }
        return bytes;
    }
}
