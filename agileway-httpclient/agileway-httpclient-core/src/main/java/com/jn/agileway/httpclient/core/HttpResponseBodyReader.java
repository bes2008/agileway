package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public interface HttpResponseBodyReader {
    boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType);

    <T> T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException;
}
