package com.jn.agileway.serialization;

import com.jn.langx.annotation.Nullable;

public interface GenericSerializer<T> {
    @Nullable
    byte[] serialize(@Nullable T obj) throws SerializationException;

    @Nullable
    T deserialize(@Nullable byte[] bytes) throws SerializationException;


    @Nullable
    T deserialize(@Nullable byte[] bytes, Class<T> targetType) throws SerializationException;

    boolean canSerialize(Class<?> type);

    Class<?> getTargetType();
}
