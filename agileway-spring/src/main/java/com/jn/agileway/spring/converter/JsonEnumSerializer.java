package com.jn.agileway.spring.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jn.langx.util.enums.Enums;

import java.io.IOException;

public class JsonEnumSerializer<E extends Enum> extends JsonSerializer<E> {
    @Override
    public void serialize(E e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(Enums.getName(e));
    }

}
