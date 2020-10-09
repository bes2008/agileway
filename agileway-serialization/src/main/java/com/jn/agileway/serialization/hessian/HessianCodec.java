package com.jn.agileway.serialization.hessian;


import com.jn.agileway.serialization.AbstractCodec;
import com.jn.agileway.serialization.CodecException;

import java.io.IOException;

public class HessianCodec<T> extends AbstractCodec<T> {

    @Override
    public byte[] encode(T o) throws CodecException {
        try {
            return Hessians.serialize(o);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Hessians.<T>deserialize(bytes);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        return null;
    }


}
