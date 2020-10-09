package com.jn.agileway.redis.core.serialization;

import com.jn.agileway.codec.Codec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.annotation.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;

public class DelegatableRedisSerializer<T> implements RedisSerializer<T> {
    private Codec<T> delegate;

    public DelegatableRedisSerializer() {

    }

    public DelegatableRedisSerializer(Codec<T> delegate) {
        setDelegate(delegate);
    }

    public Codec<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(Codec<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public byte[] serialize(T obj) throws CodecException {
        return delegate.encode(obj);
    }

    @Override
    @Nullable
    public T deserialize(byte[] bytes) throws CodecException {
        return delegate.decode(bytes);
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
