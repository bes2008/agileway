package com.jn.agileway.codec.kryo;


import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.CodecException;
import com.jn.langx.util.Emptys;

import java.io.IOException;

public class KryoCodec<T> extends AbstractCodec<T> {

    @Override
    public byte[] encode(T t) throws CodecException {
        try {
            return Kryos.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        try {
            return Kryos.deserialize(bytes);
        } catch (Throwable ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return Kryos.deserialize(bytes, targetType);
    }

}
