package com.jn.agileway.codec.serialization.activejser;

import com.jn.agileway.codec.serialization.SchemaedStruct;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import io.activej.serializer.BinaryInput;
import io.activej.serializer.BinaryOutput;
import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ActivejSerializers {
    private static final SerializerBuilder DEFAULT = SerializerBuilder.create();

    public static SerializerBuilder getSerializerBuilder() {
        return DEFAULT;
    }

    private static final ThreadLocal<BinaryOutput> outBufferFactory = new ThreadLocal<BinaryOutput>() {
        @Override
        protected BinaryOutput initialValue() {
            return new BinaryOutput(new byte[4096]);
        }
    };

    private static final ThreadLocal<BinaryInput> inputBufferFactory = new ThreadLocal<BinaryInput>() {
        @Override
        protected BinaryInput initialValue() {
            return new BinaryInput(new byte[4096]);
        }
    };

    public static byte[] serializeWithSchema(Object obj) {
        try {
            byte[] data = serialize(obj);
            SchemaedStruct struct = new SchemaedStruct();
            struct.setName(Reflects.getFQNClassName(obj.getClass()));
            struct.setValue(data);

            byte[] ret = serialize(struct);
            return ret;
        } catch (Throwable ex) {
            throw new CodecException(ex);
        }
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(obj, out);
        return out.toByteArray();
    }

    public static void serialize(Object obj, @NonNull OutputStream outputStream) throws IOException {
        Preconditions.checkNotNull(outputStream);

        BinarySerializer serializer = getSerializerBuilder().build(obj.getClass());
        serializer.encode(outBufferFactory.get(), obj);
        BinaryOutput out = outBufferFactory.get();
        outputStream.write(out.array(), 0, out.pos());
        out.pos(0);
    }

    public static <T> T deserializeWithSchema(byte[] bytes) {
        try {
            SchemaedStruct struct = deserialize(bytes, SchemaedStruct.class);
            String schema = struct.getName();
            byte[] data = struct.getValue();
            Class<T> targetClass = ClassLoaders.loadClass(schema);
            return deserialize(data, targetClass);
        } catch (Throwable throwable) {
            throw new CodecException(throwable);
        }
    }

    public static <T> T deserialize(byte[] bytes, @NonNull Class<T> targetClass) {
        Preconditions.checkNotNull(targetClass);

        BinarySerializer<T> serializer = getSerializerBuilder().build(targetClass);
        T t = serializer.decode(new BinaryInput(bytes));
        return t;
    }

}
