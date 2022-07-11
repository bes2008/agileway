package com.jn.agileway.codec.serialization.avro;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class AvroCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return new byte[0];
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return null;
    }
}
