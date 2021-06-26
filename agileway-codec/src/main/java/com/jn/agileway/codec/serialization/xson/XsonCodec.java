package com.jn.agileway.codec.serialization.xson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class XsonCodec<T> extends AbstractCodec<T> {
    @Override
    public byte[] encode(T obj) throws CodecException {
        try {
            return Xsons.serialize(obj);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        return decode(bytes, getTargetType());
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        return Xsons.deserialize(bytes, targetType);
    }
}
