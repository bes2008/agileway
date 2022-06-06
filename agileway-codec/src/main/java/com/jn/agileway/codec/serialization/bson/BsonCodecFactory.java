package com.jn.agileway.codec.serialization.bson;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class BsonCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new BsonCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.BSON;
    }
}
