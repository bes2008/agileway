package com.jn.agileway.codec.serialization.bson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class BsonCodec<T> extends AbstractCodec<T> {

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return withSchema ? Bsons.serializeWithSchema(t) : Bsons.serialize(t);
        }catch (Throwable ex){
            throw new CodecException(ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return withSchema ? Bsons.<T>deserializeWithSchema(bytes) : Bsons.deserialize(bytes, targetType);
        }catch (Throwable ex){
            throw new CodecException(ex);
        }
    }

}
