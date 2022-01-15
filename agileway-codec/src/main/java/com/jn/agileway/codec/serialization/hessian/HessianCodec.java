package com.jn.agileway.codec.serialization.hessian;


import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

import java.io.IOException;

public class HessianCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return Hessians.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return Hessians.deserialize(bytes, targetType);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

}
