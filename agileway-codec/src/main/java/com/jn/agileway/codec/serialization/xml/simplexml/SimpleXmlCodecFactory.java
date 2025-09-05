package com.jn.agileway.codec.serialization.xml.simplexml;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class SimpleXmlCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new SimpleXmlCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.SMIPLEXML;
    }
}
