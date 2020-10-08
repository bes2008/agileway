package com.jn.agileway.serialization.jdk;

import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.SerializationException;
import com.jn.langx.util.io.ObjectIOs;

import java.io.IOException;

public class JdkGenericSerializer<T> implements GenericSerializer<T> {

    @Override
    public byte[] serialize(T obj) throws SerializationException {
        try {
            return ObjectIOs.serialize(obj);
        } catch (IOException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            return ObjectIOs.deserialize(bytes);
        }catch (Throwable ex){
            throw new SerializationException(ex.getMessage(), ex);
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
