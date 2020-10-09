package com.jn.agileway.codec.protostuff;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class Protostuffs {
    private Protostuffs() {
    }

    public static <T> byte[] serialize(T o) {
        if (o == null) {
            return null;
        }
        LinkedBuffer buffer = LinkedBuffer.allocate();
        Class<T> objClass = (Class<T>) o.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(objClass);
        return ProtobufIOUtil.toByteArray(o, schema, buffer);
    }

    public static <T> T deserialize(byte[] bytes, @NonNull Class<T> targetType) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        Preconditions.checkNotNull(targetType);
        Schema<T> schema = RuntimeSchema.getSchema(targetType);
        T instance = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, instance, schema);
        return instance;
    }

}
