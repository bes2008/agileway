package com.jn.agileway.redis.core.serialization;

import com.jn.agileway.serialization.AgilewaySerializer;
import com.jn.agileway.serialization.SerializationException;
import com.jn.langx.annotation.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;

public class DelegatableRedisSerializer<T> implements RedisSerializer<T> {
    private AgilewaySerializer<T> delegate;

    public DelegatableRedisSerializer() {

    }

    public DelegatableRedisSerializer(AgilewaySerializer<T> delegate) {
        setDelegate(delegate);
    }

    public AgilewaySerializer<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(AgilewaySerializer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public byte[] serialize(T obj) throws SerializationException {
        return delegate.serialize(obj);
    }

    @Override
    @Nullable
    public T deserialize(byte[] bytes) throws SerializationException {
        return delegate.deserialize(bytes);
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return delegate.canSerialize(type);
    }

    @Override
    public Class<?> getTargetType() {
        return delegate.getTargetType();
    }
}
