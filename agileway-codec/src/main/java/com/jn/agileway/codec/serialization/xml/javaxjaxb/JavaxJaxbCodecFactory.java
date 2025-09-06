package com.jn.agileway.codec.serialization.xml.javaxjaxb;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodecFactory;

public class JavaxJaxbCodecFactory extends AbstractOXMCodecFactory {
    @Override
    protected AbstractOXMCodec newCodec() {
        return new JavaxJaxbCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JAVAX_JAXB;
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
