package com.jn.agileway.redis.core.serialization;

import com.jn.agileway.serialization.SerializationException;
import com.jn.agileway.serialization.json.EasyjsonGenericSerializer;
import com.jn.easyjson.core.JSONFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

public class EasyjsonRedisSerializer<T> implements RedisSerializer<T> {
    private EasyjsonGenericSerializer<T> delegate;

    public EasyjsonRedisSerializer() {
        setDelegate(new EasyjsonGenericSerializer<T>());
    }

    public EasyjsonGenericSerializer<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(EasyjsonGenericSerializer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return delegate.serialize(t);
    }

    @Override
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

    public void setTargetType(Class targetType) {
        delegate.setTargetType(targetType);
    }

    public JSONFactory getJsonFactory() {
        return delegate.getJsonFactory();
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        delegate.setJsonFactory(jsonFactory);
    }

    public boolean isSerializeType() {
        return delegate.isSerializeType();
    }

    public void setSerializeType(boolean serializeType) {
        delegate.setSerializeType(serializeType);
    }
}
