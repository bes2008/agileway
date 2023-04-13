package com.jn.agileway.spring.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.reflect.Reflects;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 可以直接使用 @JsonDeserializer 注解放到枚举类上即可。
 * <p>
 * 这个主要针对http 请求体中的json数据
 *
 * @param <E>
 */
public class JsonEnumDeserializer<E extends Enum> extends JsonDeserializer<E> {
    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText();
        Class beanClass = jsonParser.getCurrentValue().getClass();
        String fieldName = jsonParser.getCurrentName();
        Field field = Reflects.findField(beanClass, fieldName);
        if (field == null) {
            return null;
        }
        Class fieldClass = field.getType();
        if (!fieldClass.isEnum()) {
            return null;
        }
        return (E) Enums.ofName(fieldClass, value);
    }
}
