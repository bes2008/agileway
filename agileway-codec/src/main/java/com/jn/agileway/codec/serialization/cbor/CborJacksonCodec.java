package com.jn.agileway.codec.serialization.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.jn.agileway.codec.serialization.jackson.BasedJacksonCodec;

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
