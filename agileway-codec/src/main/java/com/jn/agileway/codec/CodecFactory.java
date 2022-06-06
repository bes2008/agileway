package com.jn.agileway.codec;

import com.jn.langx.Factory;

public interface CodecFactory extends Factory<Class, Codec> {
    @Override
    Codec get(Class expectedType);

    CodecType applyTo();
}
