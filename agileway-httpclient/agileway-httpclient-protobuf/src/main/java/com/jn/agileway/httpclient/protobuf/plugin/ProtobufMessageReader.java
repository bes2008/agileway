package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.UnsupportedResponseTypeException;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.langx.util.collection.ConcurrentReferenceHashMap;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.Reflects;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public class ProtobufMessageReader implements HttpResponsePayloadReader<GeneratedMessage> {


    private static final Map<Class<?>, Method> newBuilderMethodCache = new ConcurrentReferenceHashMap<>();
    private ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();

    private JsonFormat.Parser jsonParser = JsonFormat.parser();
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        if (expectedContentType == null || contentType == null) {
            return false;
        }
        if (!contentType.equalsTypeAndSubtype(MediaType.APPLICATION_PROTOBUF) && !contentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
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
        Message.Builder builder = getMessageBuilder((Class<? extends Message>) expectedContentType);


        if (MediaType.APPLICATION_PROTOBUF.isCompatibleWith(contentType)) {
            builder.mergeFrom(response.getPayload(), extensionRegistry);
        } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(response.getPayload()), Charsets.UTF_8);
            jsonParser.merge(reader, builder);
        }
        return (GeneratedMessage) builder.build();
    }


    private Message.Builder getMessageBuilder(Class<? extends Message> clazz) {
        try {
            Method method = newBuilderMethodCache.get(clazz);
            if (method == null) {
                method = clazz.getMethod("newBuilder");
                newBuilderMethodCache.put(clazz, method);
            }
            return (Message.Builder) method.invoke(clazz);
        } catch (Exception ex) {
            throw new UnsupportedResponseTypeException("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
        }
    }
}
