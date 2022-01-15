package com.jn.agileway.codec.serialization.xson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class XsonCodec<T> extends AbstractCodec<T> {

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return Xsons.serialize(t);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return Xsons.deserialize(bytes, targetType);
    }

}
