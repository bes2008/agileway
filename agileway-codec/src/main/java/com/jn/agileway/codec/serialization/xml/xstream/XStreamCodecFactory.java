package com.jn.agileway.codec.serialization.xml.xstream;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class XStreamCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new XStreamCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.XSTREAM;
    }
}
