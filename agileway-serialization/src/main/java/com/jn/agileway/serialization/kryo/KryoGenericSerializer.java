package com.jn.agileway.serialization.kryo;


import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.SerializationException;

import java.io.IOException;

public class KryoGenericSerializer<T> implements GenericSerializer<T> {
    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            return Kryos.serialize(t);
        } catch (IOException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            return Kryos.deserialize(bytes);
        } catch (IOException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return true;
    }

    @Override
    public Class<?> getTargetType() {
        return Object.class;
    }
}
