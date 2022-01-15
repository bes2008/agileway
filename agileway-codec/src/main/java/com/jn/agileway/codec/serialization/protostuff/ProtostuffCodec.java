package com.jn.agileway.codec.serialization.protostuff;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class ProtostuffCodec<T> extends AbstractCodec<T> {

    public ProtostuffCodec() {

    }

    public ProtostuffCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return withSchema ? Protostuffs.serializeWithSchema(t) : Protostuffs.serialize(t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return withSchema ? Protostuffs.<T>deserializeWithSchema(bytes) : Protostuffs.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }


}
