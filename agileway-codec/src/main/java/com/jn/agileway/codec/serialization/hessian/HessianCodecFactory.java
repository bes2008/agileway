package com.jn.agileway.codec.serialization.hessian;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class HessianCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new HessianCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.HESSIAN;
    }
}
