package com.jn.agileway.codec.serialization.kryo;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class KryoCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new KryoCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.KRYO;
    }
}
