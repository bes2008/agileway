package com.jn.agileway.codec.serialization.json;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.agileway.codec.serialization.SchemaedStruct;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;

public class EasyjsonCodec<T> extends AbstractCodec<T> {
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);

    public EasyjsonCodec() {
    }

    public EasyjsonCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        return withSchema ? serializeWithSchema(t) : serialize(t);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        return withSchema ? (T) deserializeWithSchema(bytes) : deserialize(bytes, targetType);
    }

    private byte[] serializeWithSchema(T obj) {
        byte[] json = serialize(obj);
        SchemaedStruct struct = new SchemaedStruct();
        struct.setName(Reflects.getFQNClassName(obj.getClass()));
        struct.setValue(json);
        return serialize(struct);
    }

    private <E> byte[] serialize(E obj) {
        String json = jsonFactory.get().toJson(obj);
        return json.getBytes(Charsets.UTF_8);
    }

    private <E> E deserializeWithSchema(byte[] bytes) {
        try {
            SchemaedStruct struct = deserialize(bytes, SchemaedStruct.class);
            Class<E> targetClass = ClassLoaders.loadClass(struct.getName());
            return deserialize(struct.getValue(), targetClass);
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }

    private <E> E deserialize(byte[] bytes, Class<E> targetType) {
        return jsonFactory.get().fromJson(new String(bytes, Charsets.UTF_8), targetType);
    }

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

}
