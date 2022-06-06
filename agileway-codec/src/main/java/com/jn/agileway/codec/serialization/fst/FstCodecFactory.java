package com.jn.agileway.codec.serialization.fst;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class FstCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new FstCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.FST;
    }
}
