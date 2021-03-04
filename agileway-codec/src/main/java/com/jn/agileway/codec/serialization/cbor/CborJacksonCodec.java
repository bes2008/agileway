package com.jn.agileway.codec.serialization.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.jn.agileway.codec.serialization.jackson.BasedJacksonCodec;

/**
 * 基于Jackson框架实现，由于Jackson框架不支持循环依赖，因而导致 Cbor 目前的实现，不支持循环依赖。
 * @param <T>
 */
public class CborJacksonCodec<T> extends BasedJacksonCodec<T> {

    public CborJacksonCodec() {
        super(new ObjectMapper(new CBORFactory()));
    }

    public CborJacksonCodec(ClassLoader classLoader) {
        super(createObjectMapper(classLoader, new ObjectMapper(new CBORFactory())));
    }

    public CborJacksonCodec(ClassLoader classLoader, CborJacksonCodec codec) {
        super(createObjectMapper(classLoader, codec.objectMapper.copy()));
    }

}
