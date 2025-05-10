package com.jn.agileway.httpclient.core;

import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;

public interface HttpResponseBodyReader<T> {
    boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType);

    T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedBodyType) throws IOException;
}
