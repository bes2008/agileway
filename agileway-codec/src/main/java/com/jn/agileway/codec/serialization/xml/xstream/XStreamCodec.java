package com.jn.agileway.codec.serialization.xml.xstream;

import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.reflect.Reflects;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class XStreamCodec<T> extends AbstractOXMCodec<T> {
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

    @Override
    protected boolean canSerializeIt(Class type) {
        return Reflects.hasAnnotation(type, XStreamAlias.class);
    }
}
