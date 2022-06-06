package com.jn.agileway.codec.serialization.xml.javabeans;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class JavaBeansXmlCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new JavaBeansXmlCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JAVABEANS_XML;
    }
}
