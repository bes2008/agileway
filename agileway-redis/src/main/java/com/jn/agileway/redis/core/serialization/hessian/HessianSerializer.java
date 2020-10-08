package com.jn.agileway.redis.core.serialization.hessian;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class HessianSerializer<T> implements RedisSerializer<T> {

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
        return null;
    }
}
