package com.jn.agileway.codec.serialization.json;

import com.jn.agileway.codec.AbstractCodec;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;

public class EasyjsonCodec<T> extends AbstractCodec<T> {
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);

    public EasyjsonCodec() {
    }

    public EasyjsonCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    protected byte[] doEncode(T t, boolean withSchema) throws CodecException {
        String json = jsonFactory.get().toJson(t);
        if (withSchema) {
            json = Reflects.getFQNClassName(Primitives.wrap(t.getClass())) + ";" + json;
        }
        return json.getBytes(Charsets.UTF_8);
    }

    @Override
    protected T doDecode(byte[] bytes, boolean withSchema, Class<T> targetType) throws CodecException {
        String json = new String(bytes, Charsets.UTF_8);
        Class<T> javaType = targetType;
        if (javaType == null) {
            javaType = getTargetType();
        }
        if (withSchema) {
            int index = json.indexOf(";");

            if (index > 0) {
                String classFQN = json.substring(0, index);
                try {
                    javaType = ClassLoaders.loadClass(classFQN);
                } catch (ClassNotFoundException ex) {
                    throw new CodecException(ex.getMessage());
                }
                json = json.substring(index + 1);
            }

        }
        if (javaType == Object.class) {
            return (T) json;
        } else {
            return jsonFactory.get().fromJson(json, javaType);
        }
    }

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

}
