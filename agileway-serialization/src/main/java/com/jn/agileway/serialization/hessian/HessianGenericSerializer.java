package com.jn.agileway.serialization.hessian;


import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.SerializationException;

import java.io.IOException;

public class HessianGenericSerializer<T> implements GenericSerializer<T> {

    @Override
    public byte[] serialize(T o) throws SerializationException {
        try {
            return Hessians.serialize(o);
        } catch (IOException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            return Hessians.<T>deserialize(bytes);
        } catch (IOException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
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
