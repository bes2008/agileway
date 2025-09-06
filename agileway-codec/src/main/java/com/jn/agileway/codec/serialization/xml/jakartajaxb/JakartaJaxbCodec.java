package com.jn.agileway.codec.serialization.xml.jakartajaxb;

import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.reflect.Reflects;
import jakarta.xml.bind.annotation.XmlRootElement;

public class JakartaJaxbCodec<T> extends AbstractOXMCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return JakartaJAXBs.marshal(t);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return JakartaJAXBs.unmarshal(bytes, targetType);
    }

    @Override
    protected boolean canSerializeIt(Class type) {
        return Reflects.hasAnnotation(type, XmlRootElement.class);
    }
}
