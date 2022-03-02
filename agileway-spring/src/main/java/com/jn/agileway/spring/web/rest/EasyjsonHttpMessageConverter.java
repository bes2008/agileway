package com.jn.agileway.spring.web.rest;


import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.jar.JarNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class EasyjsonHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    private JSONFactory jsonFactory;


    public JSONFactory getJsonFactory() {
        if (jsonFactory == null) {
            jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
        }
        return jsonFactory;
    }

    public EasyjsonHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        setDefaultCharset(Charsets.UTF_8);
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return super.canWrite(type, clazz, mediaType);
    }

    @Override
    protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        JSON j = getJsonFactory().get();
        if (j == null) {
            throw new JarNotFoundException("the jar not found: easyjson-gson.jar or easyjson-fastjson.jar or easyjson-jackson.jar");
        }
        String json = j.toJson(o);
        if (json != null) {
            OutputStream out = outputMessage.getBody();
            IOs.write(json, outputMessage.getBody());
            out.flush();
        }
    }

    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readType(clazz, inputMessage);
    }

    @Override
    public Object read(Type type, Class contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readType(type, inputMessage);
    }

    private Object readType(Type type, HttpInputMessage inputMessage) {
        try {
            JSON j = getJsonFactory().get();
            InputStream in = inputMessage.getBody();
            Reader reader = new InputStreamReader(in, getCharset(inputMessage.getHeaders()));
            Object o = j.fromJson(reader, type);
            return o;
        } catch (Throwable ex) {
            throw new HttpMessageNotReadableException(ex.getMessage(), ex);
        }
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
            return Charsets.UTF_8;
        }
        return headers.getContentType().getCharset();
    }
}
