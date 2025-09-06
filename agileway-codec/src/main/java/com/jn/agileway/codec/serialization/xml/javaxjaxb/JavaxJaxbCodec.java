package com.jn.agileway.codec.serialization.xml.javaxjaxb;

import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.langx.codec.CodecException;

public class JavaxJaxbCodec<T> extends AbstractOXMCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return JavaxJAXBs.marshal(t);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return JavaxJAXBs.unmarshal(bytes, targetType);
    }
}