package com.jn.agileway.serialization;

import com.jn.langx.annotation.Nullable;

public interface AgilewaySerializer<T> {
    @Nullable
    byte[] serialize(@Nullable T obj) throws SerializationException;

    @Nullable
    T deserialize(@Nullable byte[] bytes) throws SerializationException;

    boolean canSerialize(Class<?> type);

    Class<?> getTargetType();
}
