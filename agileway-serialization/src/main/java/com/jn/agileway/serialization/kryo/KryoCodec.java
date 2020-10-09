package com.jn.agileway.serialization.kryo;


import com.jn.agileway.serialization.Codec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.util.Emptys;

import java.io.IOException;

public class KryoCodec<T> implements Codec<T> {

    private Class<T> targetType;

    @Override
    public byte[] serialize(T t) throws CodecException {
        try {
            return Kryos.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws CodecException {
        try {
            return Kryos.deserialize(bytes);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return Kryos.deserialize(bytes, targetType);
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return true;
    }

    public void setTargetType(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public Class<?> getTargetType() {
        return targetType;
    }
}
