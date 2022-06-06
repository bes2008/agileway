package com.jn.agileway.codec;

public abstract class AbstractCodecFactory implements CodecFactory {
    @Override
    public final Codec get(Class expectedType) {
        AbstractCodec codec = newCodec();
        if (expectedType != null) {
            codec.setTargetType(expectedType);
        }
        return codec;
    }

    abstract protected AbstractCodec newCodec();
}
