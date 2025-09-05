package com.jn.agileway.codec.serialization.xml.simplexml;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodecFactory;

public class SimpleXmlCodecFactory extends AbstractOXMCodecFactory {
    @Override
    protected AbstractOXMCodec newCodec() {
        return new SimpleXmlCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.SMIPLEXML;
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
