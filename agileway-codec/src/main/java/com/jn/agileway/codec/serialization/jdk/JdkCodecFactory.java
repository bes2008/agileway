package com.jn.agileway.codec.serialization.jdk;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class JdkCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new JdkCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JDK;
    }
}
