package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.GeneratedMessage;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Type;

public class ProtobufMessageReader implements HttpResponsePayloadReader<GeneratedMessage> {
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        if (expectedContentType == null || contentType == null) {
            return false;
        }
        if (!contentType.equalsTypeAndSubtype(MediaType.APPLICATION_PROTOBUF)) {
            return false;
        }
        if (!(expectedContentType instanceof Class)) {
            return false;
        }
        Class clazz = (Class) expectedContentType;
        if (!Reflects.isSubClass(GeneratedMessage.class, clazz)) {
            return false;
        }
        return true;
    }

    @Override
    public GeneratedMessage read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        return null;
    }
}
