package com.jn.agileway.codec.serialization.fse;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class FseCodecFactory  extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new FseCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.FSE;
    }
}
