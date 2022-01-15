package com.jn.agileway.codec.serialization.jdk;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.io.ObjectIOs;

import java.io.IOException;

public class JdkCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return ObjectIOs.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return ObjectIOs.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }
}
