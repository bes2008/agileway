package com.jn.agileway.codec.serialization.activejser;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;
/**
 * 要求字段得 是 public,
 * 要求必须使用 @Serialize 或者 @Deserialize 注解
 */
public class ActivejSerCodec<T> extends AbstractCodec<T> {

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return withSchema ? ActivejSerializers.serializeWithSchema(t) : ActivejSerializers.serialize(t);
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return withSchema ? ActivejSerializers.<T>deserializeWithSchema(bytes) : ActivejSerializers.<T>deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }
}
