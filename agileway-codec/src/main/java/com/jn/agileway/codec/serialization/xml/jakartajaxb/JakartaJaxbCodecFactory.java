package com.jn.agileway.codec.serialization.xml.jakartajaxb;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodecFactory;

public class JakartaJaxbCodecFactory extends AbstractOXMCodecFactory {
    @Override
    protected AbstractOXMCodec newCodec() {
        return new JakartaJaxbCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JAKARTA_JAXB;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
