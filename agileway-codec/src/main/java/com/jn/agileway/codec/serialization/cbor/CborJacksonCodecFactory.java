package com.jn.agileway.codec.serialization.cbor;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class CborJacksonCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new CborJacksonCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.CBOR;
    }
}
