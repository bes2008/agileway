package com.jn.agileway.codec.serialization.xml.simplexml;

import com.jn.langx.Factory;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SimpleXMLs {
    private static final ThreadLocalFactory<?, Serializer> serializerFactory = new ThreadLocalFactory<Object, Serializer>(new Factory<Object, Serializer>() {
        @Override
        public Serializer get(Object key) {
            Serializer serializer = new Persister();
            return serializer;
        }
    });

    public static void serialize(Serializer serializer, Object bean, OutputStream out) {
        try {
            serializer.write(bean, out);
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public static byte[] serialize(Serializer serializer, Object bean) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        serialize(serializer, bean, bao);
        return bao.toByteArray();
    }

    public static byte[] serialize(Object bean) {
        return serialize(serializerFactory.get(), bean);
    }

    public static <T> T deserialize(Serializer serializer, byte[] bytes, Class<T> targetType) {
        try {
            return serializer.read(targetType, new ByteArrayInputStream(bytes), false);
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> targetType) {
        return deserialize(serializerFactory.get(), bytes, targetType);
    }
}
