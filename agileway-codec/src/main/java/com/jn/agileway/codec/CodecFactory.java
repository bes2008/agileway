package com.jn.agileway.codec;

import com.jn.langx.Factory;
import com.jn.langx.Named;

public interface CodecFactory extends Factory<Class, Codec>, Named {
    @Override
    Codec get(Class expectedType);

    CodecType applyTo();

    @Override
    String getName();
}
