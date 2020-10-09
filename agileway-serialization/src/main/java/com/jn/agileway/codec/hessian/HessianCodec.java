package com.jn.agileway.codec.hessian;


import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;

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
