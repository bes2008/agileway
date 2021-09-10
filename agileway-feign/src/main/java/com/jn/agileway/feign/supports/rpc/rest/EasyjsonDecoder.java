package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.JsonException;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @since 2.6.0
 */
public class EasyjsonDecoder implements Decoder {
    private JSONFactory jsonFactory;

    public EasyjsonDecoder() {
        setJsonFactory(JsonFactorys.getJSONFactory(JsonScope.SINGLETON));
    }

    public EasyjsonDecoder(JSONFactory jsonFactory) {
        setJsonFactory(jsonFactory);
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response == null || response.body() == null) {
            return null;
        }
        Reader reader = response.body().asReader();
        try {
            Object obj = jsonFactory.get().fromJson(reader, type);
            return obj;
        } catch (JsonException ex) {
            throw new DecodeException(ex.getMessage(), ex);
        }
    }
}
