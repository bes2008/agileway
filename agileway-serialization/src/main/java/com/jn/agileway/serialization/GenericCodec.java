package com.jn.agileway.serialization;

import com.jn.langx.annotation.Nullable;

public interface GenericCodec<T> {
    @Nullable
    byte[] serialize(@Nullable T obj) throws CodecException;

    @Nullable
    T deserialize(@Nullable byte[] bytes) throws CodecException;


    @Nullable
    T deserialize(@Nullable byte[] bytes, Class<T> targetType) throws CodecException;

    boolean canSerialize(Class<?> type);

    Class<?> getTargetType();
}
