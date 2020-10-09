package com.jn.agileway.codec.jdk;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.util.io.ObjectIOs;

import java.io.IOException;

public class JdkCodec<T> extends AbstractCodec<T> {

    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return ObjectIOs.serialize(obj);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return ObjectIOs.deserialize(bytes);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return ObjectIOs.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }
}
