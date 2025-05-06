package com.jn.agileway.redis.core.serialization;

import com.jn.agileway.codec.Codec;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
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
        Class<T> targetType = (Class<T>) getTargetType();
        if (targetType == null) {
            return delegate.decode(bytes);
        } else {
            return delegate.decode(bytes, targetType);
        }
    }

    public boolean canSerialize(Class<?> type) {
        return delegate.canSerialize(type);
    }

    public Class<?> getTargetType() {
        return delegate.getTargetType();
    }
}
