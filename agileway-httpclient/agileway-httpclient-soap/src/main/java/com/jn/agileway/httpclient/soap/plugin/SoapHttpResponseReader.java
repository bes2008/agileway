package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public class SoapHttpResponseReader implements HttpResponseContentReader {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        return false;
    }

    @Override
    public Object read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws IOException {
        return null;
    }
}
