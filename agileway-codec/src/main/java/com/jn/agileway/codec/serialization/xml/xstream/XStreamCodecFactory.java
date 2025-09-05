package com.jn.agileway.codec.serialization.xml.xstream;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodec;
import com.jn.agileway.codec.serialization.xml.AbstractOXMCodecFactory;

public class XStreamCodecFactory extends AbstractOXMCodecFactory {
    @Override
    protected AbstractOXMCodec newCodec() {
        return new XStreamCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.XSTREAM;
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
