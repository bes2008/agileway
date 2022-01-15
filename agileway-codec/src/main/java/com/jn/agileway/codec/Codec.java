package com.jn.agileway.codec;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.codec.ICodec;

public interface Codec<T> extends ICodec<T> {

    /**
     * 序列化
     * @param t
     * @return
     * @throws CodecException
     */
    @Override
    byte[] encode(T t) throws CodecException;

    /**
     * 反序列化
     * @param bytes
     * @return
     * @throws CodecException
     */
    @Override
    T decode(byte[] bytes) throws CodecException;

    /**
     * 按照指定的类型反序列化，
     *
     * @param bytes
     * @param targetType
     * @return
     * @throws CodecException
     */
    @Nullable
    T decode(@Nullable byte[] bytes,@NonNull Class<T> targetType) throws CodecException;

    boolean canSerialize(Class<?> type);

    Class<T> getTargetType();

    void setTargetType(Class<T> expectedTargetType);
}
