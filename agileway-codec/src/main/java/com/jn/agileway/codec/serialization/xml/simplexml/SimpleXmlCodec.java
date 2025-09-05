package com.jn.agileway.codec.serialization.xml.simplexml;

import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.langx.codec.CodecException;

public class SimpleXmlCodec<T> extends AbstractOXMCodec<T> {
    public SimpleXmlCodec() {
    }

    public SimpleXmlCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return SimpleXMLs.serialize(t);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return SimpleXMLs.deserialize(bytes, targetType);
    }
}
