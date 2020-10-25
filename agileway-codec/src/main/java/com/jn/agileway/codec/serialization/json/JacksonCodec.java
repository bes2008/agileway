package com.jn.agileway.codec.serialization.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.agileway.codec.serialization.jackson.BasedJacksonCodec;

public class JacksonCodec<T> extends BasedJacksonCodec<T> {
    public JacksonCodec() {
        super(new ObjectMapper(new JsonFactory()));
    }

    public JacksonCodec(ClassLoader classLoader) {
        super(createObjectMapper(classLoader, new ObjectMapper(new JsonFactory())));
    }

    public JacksonCodec(ClassLoader classLoader, JacksonCodec codec) {
        super(createObjectMapper(classLoader, codec.objectMapper.copy()));
    }

}
