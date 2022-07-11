package com.jn.agileway.codec.serialization.avro;

import com.jn.agileway.codec.serialization.SchemaedStruct;
import com.jn.langx.Factory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class Avros {

    private static final Logger logger = Loggers.getLogger(Avros.class);

    public static final ThreadLocalFactory<?, BinaryEncoder> encoderFactoryLocal = new ThreadLocalFactory<Object, BinaryEncoder>(new Factory<Object, BinaryEncoder>() {
        @Override
        public BinaryEncoder get(Object o) {
            return EncoderFactory.get().binaryEncoder(new ByteArrayOutputStream(), null);
        }
    });

    public static final ThreadLocalFactory<?, BinaryDecoder> decoderFactoryLocal = new ThreadLocalFactory<Object, BinaryDecoder>(new Factory<Object, BinaryDecoder>() {
        @Override
        public BinaryDecoder get(Object o) {
            return DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(new byte[0]), null);
        }
    });

    private static ConcurrentHashMap<Class, Schema> schemaMap = new ConcurrentHashMap<Class, Schema>();

    public static Schema getSchema(Class targetClass) {
        Preconditions.checkNotNull(targetClass, "the target class is null");
        Schema schema = schemaMap.get(targetClass);
        if (schema == null) {
            synchronized (Avros.class) {
                schema = schemaMap.get(targetClass);
                if (schema == null) {
                    schema = ReflectData.AllowNull.get().getSchema(targetClass);
                    if (schema != null) {
                        schemaMap.putIfAbsent(targetClass, schema);
                    }
                }
            }
        }
        return schema;
    }

    public static <T> byte[] serializeWithSchema(T o) throws IOException {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            serializeWithSchema(o, bao);
            return bao.toByteArray();
        } finally {
            IOs.close(bao);
        }
    }

    public static <T> void serializeWithSchema(@Nullable T o, @NonNull OutputStream outputStream) throws IOException {
        if (o == null) {
            return;
        }

        // 序列化 对象
        byte[] data = serialize(o);

        // 将 对象进行包装，再次写数据
        SchemaedStruct wrappedStruct = new SchemaedStruct();
        wrappedStruct.setName(Reflects.getFQNClassName(o.getClass()));
        wrappedStruct.setValue(data);
        serialize(wrappedStruct, outputStream);
    }


    public static <T> byte[] serialize(@Nullable T o) throws IOException {
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            serialize(o, bao);
            return bao.toByteArray();
        } finally {
            IOs.close(bao);
        }
    }

    public static <T> void serialize(@Nullable T o, OutputStream outputStream) throws IOException {
        ReflectDatumWriter writer = new ReflectDatumWriter(o.getClass(), ReflectData.AllowNull.get());
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, encoderFactoryLocal.get());
        writer.write(o, encoder);
        encoder.flush();
    }


    public static <T> T deserialize(byte[] bytes, @NonNull Class<T> targetType) throws IOException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }

        Schema schema = getSchema(targetType);
        ReflectDatumReader<T> reader = new ReflectDatumReader<T>(schema);
        T t = reader.read(null, DecoderFactory.get().binaryDecoder(bytes, decoderFactoryLocal.get()));
        return t;
    }

    public static <T> T deserializeWithSchema(byte[] bytes) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        try {
            SchemaedStruct wrappedStruct = deserialize(bytes, SchemaedStruct.class);

            byte[] data = wrappedStruct.getValue();
            String actualClass = wrappedStruct.getName();
            Class<T> targetType = ClassLoaders.loadClass(actualClass);
            return deserialize(data, targetType);
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }


}
