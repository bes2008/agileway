package com.jn.agileway.codec.serialization.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.serialization.kryo.KryoCustomizer;

public class LangxJavaKryoCustomizer implements KryoCustomizer {
    @Override
    public String getName() {
        return "langx_java";
    }

    @Override
    public void customize(Kryo kryo) {

    }

}
