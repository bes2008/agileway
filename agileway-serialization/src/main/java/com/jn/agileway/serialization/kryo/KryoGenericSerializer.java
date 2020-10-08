package com.jn.agileway.serialization.kryo;


import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.SerializationException;
import com.jn.langx.util.Emptys;

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
    public T deserialize(byte[] bytes, Class<T> targetType) throws SerializationException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return Kryos.deserialize(bytes, targetType);
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
