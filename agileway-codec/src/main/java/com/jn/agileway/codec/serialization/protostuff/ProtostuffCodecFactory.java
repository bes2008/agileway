package com.jn.agileway.codec.serialization.protostuff;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class ProtostuffCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new ProtostuffCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.PROTOSTUFF;
    }
}
