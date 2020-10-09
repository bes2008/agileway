package com.jn.agileway.serialization.protostuff;

import com.jn.agileway.serialization.Codec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.util.reflect.Reflects;

public class ProtostuffCodec<T> implements Codec<T> {

    private Class<T> targetType;

    public ProtostuffCodec() {

    }

    public ProtostuffCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    public byte[] serialize(T obj) throws CodecException {
        try {
            return Protostuffs.serialize(obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws CodecException {
        return deserialize(bytes, (Class<T>) getTargetType());
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return Protostuffs.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        if (this.targetType == null) {
            return true;
        }
        return Reflects.isSubClassOrEquals(type, targetType);
    }

    public void setTargetType(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public Class<?> getTargetType() {
        return targetType;
    }
}
