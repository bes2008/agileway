package com.jn.agileway.codec.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.kryo.KryoCustomizer;

public class JodaTimeKryoCustomizer implements KryoCustomizer {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public void customize(Kryo kryo) {

    }
}
