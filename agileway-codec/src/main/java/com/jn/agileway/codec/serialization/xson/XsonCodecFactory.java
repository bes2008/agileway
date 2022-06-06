package com.jn.agileway.codec.serialization.xson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class XsonCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new XsonCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.XSON;
    }
}
