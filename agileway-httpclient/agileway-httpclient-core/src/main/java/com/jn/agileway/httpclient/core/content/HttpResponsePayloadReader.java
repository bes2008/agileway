package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public interface HttpResponsePayloadReader<T> {
    boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType);

    T read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws Exception;
}
