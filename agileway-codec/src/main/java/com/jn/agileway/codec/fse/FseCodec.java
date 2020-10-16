package com.jn.agileway.codec.fse;

import com.jfireframework.fse.Fse;
import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;

public class FseCodec<T> extends AbstractCodec<T> {
    private Fse fse = new Fse();

    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return Fses.serialize(fse, obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Fses.deserialize(fse, bytes);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return Fses.deserialize(fse, bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }
}