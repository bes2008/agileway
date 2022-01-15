package com.jn.agileway.codec.serialization.bson;

import com.jn.agileway.codec.serialization.SchemaedStruct;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.io.BasicOutputBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class Bsons {
    private static final CodecRegistry codecRegistry;

    static {
        Iterator<CodecProvider> providerIterator = ServiceLoader.load(CodecProvider.class).iterator();
        List<CodecProvider> providers = Collects.newArrayList();
        while (providerIterator.hasNext()) {
            try {
                CodecProvider provider = providerIterator.next();
                providers.add(provider);
            }catch (Throwable ex){
                Loggers.getLogger(Bsons.class).warn(ex.getMessage(),ex);
            }
        }

        // Pojo
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        providers.add(pojoCodecProvider);

        // java.lang.Object

        codecRegistry = CodecRegistries.fromProviders(providers);
    }

    public static <T> T deserializeWithSchema(byte[] bytes) {
        Class<SchemaedStruct> structClass = SchemaedStruct.class;
        SchemaedStruct struct = deserialize(bytes, structClass);
        byte[] data = struct.getValue();
        String schema = struct.getName();
        try {
            Class<T> tClass = ClassLoaders.loadClass(schema);
            return deserialize(data, tClass);
        } catch (ClassNotFoundException ex) {
            throw new CodecException(ex);
        }
    }

    public static <T> T deserialize(byte[] bytes, @NonNull Class<T> targetClass) {
        Preconditions.checkNotNull(targetClass);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BsonBinaryReader reader = new BsonBinaryReader(buffer);
        Codec<T> codec = codecRegistry.get(targetClass);
        DecoderContext decoderContext = DecoderContext.builder()
                .build();
        T t = codec.decode(reader, decoderContext);
        return t;
    }


    public static <T> byte[] serializeWithSchema(T obj){
        try {
            byte[] objBytes = serialize(obj);
            SchemaedStruct struct = new SchemaedStruct();
            struct.setName(Reflects.getFQNClassName(obj.getClass()));
            struct.setValue(objBytes);
            return serialize(struct);
        }catch (IOException ex){
            throw new CodecException(ex);
        }
    }

    /**
     * 序列化对象本身
     */
    public static <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            serialize(obj, stream);
            return stream.toByteArray();
        } finally {
            IOs.close(stream);
        }
    }

    /**
     * 序列化对象本身
     */
    public static <T> void serialize(T obj, @NonNull OutputStream stream) throws IOException {
        serialize(obj, null, stream);
    }

    /**
     * 序列化对象本身
     */
    public static <T> void serialize(T obj, @Nullable BsonBinaryWriter writer, @NonNull OutputStream stream) throws IOException {
        Preconditions.checkNotNull(stream);

        BasicOutputBuffer buffer = null;
        if (writer == null) {
            writer = new BsonBinaryWriter(new BasicOutputBuffer());
        }
        buffer = (BasicOutputBuffer) writer.getBsonOutput();
        Class<T> tClass = (Class<T>) obj.getClass();
        Codec<T> codec = codecRegistry.get(tClass);
        EncoderContext encoderContext = EncoderContext.builder()
                .isEncodingCollectibleDocument(true)
                .build();
        codec.encode(writer, obj, encoderContext);
        writer.flush();
        buffer.pipe(stream);
    }

}
