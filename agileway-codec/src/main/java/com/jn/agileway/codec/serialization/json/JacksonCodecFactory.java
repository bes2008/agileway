package com.jn.agileway.codec.serialization.json;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class JacksonCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new JacksonCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.JACKSON;
    }
}
