package com.jn.agileway.serialization.json;

import com.jn.agileway.serialization.AbstractCodec;
import com.jn.agileway.serialization.CodecException;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;

public class EasyjsonCodec<T> extends AbstractCodec<T> {
    private boolean serializeType = false;
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);

    public EasyjsonCodec() {
    }

    public EasyjsonCodec(Class<T> targetType) {
        setTargetType(targetType);
    }

    @Override
    public byte[] encode(T t) throws CodecException {
        if (t == null) {
            return new byte[0];
        }
        String json = jsonFactory.get().toJson(t);
        if (serializeType) {
            json = Reflects.getFQNClassName(Primitives.wrap(t.getClass())) + ";" + json;
        }
        return json.getBytes(Charsets.UTF_8);
    }

    @Override
    public T decode(byte[] bytes) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        String json = new String(bytes, Charsets.UTF_8);
        Class<T> javaType = getTargetType();
        if (serializeType) {
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

    @Override
    public T decode(byte[] bytes, Class<T> targetType) throws CodecException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        String json = new String(bytes, Charsets.UTF_8);
        return jsonFactory.get().fromJson(json, targetType);
    }


    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public boolean isSerializeType() {
        return serializeType;
    }

    public void setSerializeType(boolean serializeType) {
        this.serializeType = serializeType;
    }
}
