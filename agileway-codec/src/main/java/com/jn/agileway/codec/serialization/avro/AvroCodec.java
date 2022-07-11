package com.jn.agileway.codec.serialization.avro;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

import java.io.IOException;

public class AvroCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return withSchema ? Avros.serializeWithSchema(t) : Avros.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return withSchema ? Avros.<T>deserializeWithSchema(bytes) : Avros.<T>deserialize( bytes, targetType);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
