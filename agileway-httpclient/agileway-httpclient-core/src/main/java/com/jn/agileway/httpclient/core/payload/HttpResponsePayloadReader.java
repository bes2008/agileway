package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public interface HttpResponsePayloadReader<T> {
    boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType);

    T read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception;
}
