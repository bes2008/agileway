package com.jn.agileway.codec;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.codec.ICodec;

public interface Codec<T> extends ICodec<T> {
    @Override
    T decode(byte[] bytes) throws CodecException;

    @Override
    byte[] encode(T t) throws CodecException;

    @Nullable
    T decode(@Nullable byte[] bytes, Class<T> targetType) throws CodecException;

    boolean canSerialize(Class<?> type);

    Class<T> getTargetType();

    void setTargetType(Class<T> targetType);
}
