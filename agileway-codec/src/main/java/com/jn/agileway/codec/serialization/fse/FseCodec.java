package com.jn.agileway.codec.serialization.fse;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;

public class FseCodec<T> extends AbstractCodec<T> {

    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return Fses.serialize(obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Fses.deserialize(bytes, getTargetType());
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return Fses.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }
}