package com.jn.agileway.codec.serialization.activejser;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class ActivejCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new ActivejSerCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.ACTIVEJ;
    }
}
