package com.jn.agileway.codec.serialization.bson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class BsonCodec<T> extends AbstractCodec<T> {


    @Override
    public byte[] encode(T t) throws CodecException {
        return Bsons.serializeWithSchema(t);
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        return Bsons.deserializeWithSchema(bytes);
    }

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        return Bsons.deserializeWithSchema(bytes);
    }
}
