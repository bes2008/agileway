package com.jn.agileway.httpclient.protobuf.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public class ProtobufMessageReader implements HttpResponsePayloadReader<Object> {
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        return false;
    }

    @Override
    public Object read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        return null;
    }
}
