package com.jn.agileway.codec.serialization.avro;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.langx.codec.CodecException;

import java.io.IOException;

/**
 * 用来进行 序列化的对象，需要满足如下要求：
 * 1）类中字段的类型，尽可能要具体。譬如不能是 直接声明为 Serializable
 * 2）类中字段类型为 map时，map的key 类型只能是  String
 * @param <T>
 */
public class AvroCodec<T> extends AbstractCodec<T> {
    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        try {
            return withSchema ? Avros.serializeWithSchema(t) : Avros.serialize(t);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        try {
            return withSchema ? Avros.<T>deserializeWithSchema(bytes) : Avros.<T>deserialize( bytes, targetType);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
