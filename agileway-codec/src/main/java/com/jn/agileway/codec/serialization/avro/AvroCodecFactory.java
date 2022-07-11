package com.jn.agileway.codec.serialization.avro;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class AvroCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new AvroCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.AVRO;
    }
}
