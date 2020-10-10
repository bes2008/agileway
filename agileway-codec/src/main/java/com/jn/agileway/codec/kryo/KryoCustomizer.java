package com.jn.agileway.codec.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoCustomizer {
    /**
     * 名字一旦定义好，不可再修改
     */
    String getName();
    void customize(Kryo kryo);
}
