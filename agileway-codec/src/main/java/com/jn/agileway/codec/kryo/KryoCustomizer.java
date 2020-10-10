package com.jn.agileway.codec.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoCustomizer {
    String getName();
    void customize(Kryo kryo);
}
