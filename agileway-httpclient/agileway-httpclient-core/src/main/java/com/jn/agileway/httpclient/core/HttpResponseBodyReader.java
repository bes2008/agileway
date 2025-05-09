package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.mime.MediaType;

public interface HttpResponseBodyReader {
    boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Class<?> clazz);

    <T> T read(UnderlyingHttpResponse response, MediaType contentType, Class<T> clazz);
}
