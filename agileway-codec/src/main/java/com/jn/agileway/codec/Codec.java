package com.jn.agileway.codec;

import com.jn.langx.annotation.Nullable;

public interface Codec<T> {
    @Nullable
    byte[] encode(@Nullable T obj) throws CodecException;

    @Nullable
    T decode(@Nullable byte[] bytes) throws CodecException;


    @Nullable
    T decode(@Nullable byte[] bytes, Class<T> targetType) throws CodecException;

    boolean canSerialize(Class<?> type);

    Class<T> getTargetType();
}
