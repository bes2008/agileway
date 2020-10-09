package com.jn.agileway.serialization.hessian;


import com.jn.agileway.serialization.Codec;
import com.jn.agileway.serialization.CodecException;

import java.io.IOException;

public class HessianCodec<T> implements Codec<T> {

    @Override
    public byte[] serialize(T o) throws CodecException {
        try {
            return Hessians.serialize(o);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws CodecException {
        try {
            return Hessians.<T>deserialize(bytes);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> targetType) throws CodecException {
        return null;
    }

    @Override
    public boolean canSerialize(Class type) {
        return true;
    }

    @Override
    public Class<?> getTargetType() {
        return Object.class;
    }
}
