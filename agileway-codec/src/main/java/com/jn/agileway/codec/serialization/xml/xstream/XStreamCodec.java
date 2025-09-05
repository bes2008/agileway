package com.jn.agileway.codec.serialization.xml.xstream;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

public class XStreamCodec<T> extends AbstractCodec<T> {
    public XStreamCodec() {

    }

    public XStreamCodec(Class expectedType) {
        setTargetType(expectedType);
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return XStreams.serialize(t);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return XStreams.deserialize(bytes, targetType);
    }
}
