package com.jn.agileway.codec.serialization.xml.javabeans;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodecFactory;

public class JavaBeansXmlCodecFactory extends AbstractOXMCodecFactory {
    @Override
    protected AbstractOXMCodec newCodec() {
        return new JavaBeansXmlCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JAVABEANS_XML;
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
