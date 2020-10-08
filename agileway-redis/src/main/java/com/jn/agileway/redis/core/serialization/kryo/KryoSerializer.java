package com.jn.agileway.redis.core.serialization.kryo;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class KryoSerializer<T> implements RedisSerializer<T> {
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
