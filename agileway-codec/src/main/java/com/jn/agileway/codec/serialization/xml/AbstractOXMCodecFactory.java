package com.jn.agileway.codec.serialization.xml;

import com.jn.agileway.codec.AbstractCodecFactory;
import com.jn.agileway.codec.CodecType;
import com.jn.langx.Ordered;

public abstract class AbstractOXMCodecFactory extends AbstractCodecFactory implements Ordered {
    @Override
    protected AbstractOXMCodec newCodec() {
        return null;
    }

    @Override
    public CodecType applyTo() {
        return CodecType.XML;
    }

}
