package com.jn.agileway.serialization.jdk;

import com.jn.agileway.serialization.Codec;
import com.jn.agileway.serialization.CodecException;
import com.jn.langx.util.io.ObjectIOs;

import java.io.IOException;

public class JdkCodec<T> implements Codec<T> {

    @Override
    public byte[] serialize(T obj) throws CodecException {
        try {
            return ObjectIOs.serialize(obj);
        } catch (IOException ex) {
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws CodecException {
        try {
            return ObjectIOs.deserialize(bytes);
        }catch (Throwable ex){
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> targetType) throws CodecException {
        try {
            return ObjectIOs.deserialize(bytes, targetType);
        }catch (Throwable ex){
            throw new CodecException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return true;
    }

    @Override
    public Class<?> getTargetType() {
        return Object.class;
    }
}
