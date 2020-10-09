package com.jn.agileway.redis.core.serialization;

import com.jn.agileway.serialization.GenericCodec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.annotation.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;

public class DelegatableRedisSerializer<T> implements RedisSerializer<T> {
    private GenericCodec<T> delegate;

    public DelegatableRedisSerializer() {

    }

    public DelegatableRedisSerializer(GenericCodec<T> delegate) {
        setDelegate(delegate);
    }

    public GenericCodec<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(GenericCodec<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public byte[] serialize(T obj) throws CodecException {
        return delegate.serialize(obj);
    }

    @Override
    @Nullable
    public T deserialize(byte[] bytes) throws CodecException {
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
