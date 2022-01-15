package com.jn.agileway.codec.serialization.fse;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class FseCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return Fses.serialize(t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return Fses.deserialize(bytes, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

}