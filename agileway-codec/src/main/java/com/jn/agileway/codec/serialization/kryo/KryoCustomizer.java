package com.jn.agileway.codec.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoCustomizer {
    String getName();
    void customize(Kryo kryo);
}
