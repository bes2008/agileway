package com.jn.agileway.codec.serialization.msgpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.agileway.codec.serialization.jackson.BasedJacksonCodec;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MsgPackCodec<T> extends BasedJacksonCodec<T> {
    public MsgPackCodec() {
        super(new ObjectMapper(new MessagePackFactory()));
    }

    public MsgPackCodec(ClassLoader classLoader) {
        super(createObjectMapper(classLoader, new ObjectMapper(new MessagePackFactory())));
    }

    public MsgPackCodec(ClassLoader classLoader, MsgPackCodec codec) {
        super(createObjectMapper(classLoader, codec.objectMapper.copy()));
    }
}
