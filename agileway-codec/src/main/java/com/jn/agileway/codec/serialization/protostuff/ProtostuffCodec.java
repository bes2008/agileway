package com.jn.agileway.codec.serialization.protostuff;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.reflect.Reflects;

public class ProtostuffCodec<T> extends AbstractCodec<T> {

    public ProtostuffCodec() {

    }

    public ProtostuffCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return Protostuffs.serializeWithSchema(obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        return decode(bytes, (Class<T>) getTargetType());
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return Protostuffs.deserializeWithSchema(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean canSerialize(Class type) {
        return type != null && type != Object.class && Reflects.isSubClassOrEquals(type, getTargetType());
    }
}
