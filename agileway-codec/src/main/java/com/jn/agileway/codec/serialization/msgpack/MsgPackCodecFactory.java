package com.jn.agileway.codec.serialization.msgpack;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;

public class MsgPackCodecFactory extends AbstractCodecFactory {
    @Override
    protected AbstractCodec newCodec() {
        return new MsgPackCodec();
    }

    @Override
    public CodecType applyTo() {
        return CodecType.MSGPACK;
    }
}
