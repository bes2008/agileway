package com.jn.agileway.codec.serialization.fse;

import com.jfireframework.fse.ByteArray;
import com.jfireframework.fse.Fse;
import com.jn.agileway.codec.serialization.fst.Fsts;
import com.jn.langx.Factory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Pair;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fses {
    private Fses() {
    }

    private static final Logger logger = Loggers.getLogger(Fsts.class);

    private static final Map<String, FseCustomizer> fseCustomizerRegistry = new ConcurrentHashMap<String, FseCustomizer>();


    public static final ThreadLocalFactory<?, Pair<Fse, ByteArray>> fseFactory = new ThreadLocalFactory<Object, Pair<Fse, ByteArray>>(new Factory<Object, Pair<Fse, ByteArray>>() {
        @Override
        public Pair<Fse, ByteArray> get(Object o) {
            final Fse fse = new Fse();
            // 用于解决循环依赖
            Collects.forEach(fseCustomizerRegistry, new Consumer2<String, FseCustomizer>() {
                @Override
                public void accept(String name, FseCustomizer customizer) {
                    try {
                        customizer.customize(fse);
                    } catch (Throwable ex) {
                        if (ex instanceof NoClassDefFoundError || ex instanceof ClassNotFoundException) {
                            logger.error("Error occur when register FSE serializers for {}, may be you should append de.javakaffee:kryo-serializers.jar to the classpath", name);
                        } else {
                            logger.error("Error occur when register FSE customizer  for {}", name);
                        }
                    }
                }
            });
            ByteArray byteArray = ByteArray.allocate(256);
            return new Entry<Fse, ByteArray>(fse, byteArray);
        }
    });

    public static <T> byte[] serialize(@Nullable T o) throws IOException {
        Pair<Fse, ByteArray> pair = fseFactory.get();
        Fse fse = pair.getKey();
        ByteArray buffer = pair.getValue();
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            serialize(fse, buffer, o, bao);
            return bao.toByteArray();
        } finally {
            buffer.clear();
            IOs.close(bao);
        }
    }

    public static <T> void serialize(@Nullable T o, OutputStream outputStream) throws IOException {
        Pair<Fse, ByteArray> pair = fseFactory.get();
        Fse fse = pair.getKey();
        ByteArray buffer = pair.getValue();
        serialize(fse, buffer, o, outputStream);
        buffer.clear();
    }

    public static <T> byte[] serialize(@NonNull Fse fse, @Nullable T o) throws IOException {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            serialize(fse, ByteArray.allocate(256), o, bao);
            return bao.toByteArray();
        } finally {
            IOs.close(bao);
        }
    }

    public static <T> void serialize(@NonNull Fse fse, ByteArray buffer, @NonNull T o, @NonNull OutputStream outputStream) throws IOException {
        if (o != null) {
            fse.serialize(o, buffer);
            byte[] bytes = buffer.toArray();
            outputStream.write(bytes);
        }
    }

    public static <T> T deserialize(@Nullable byte[] bytes) {
        Pair<Fse, ByteArray> pair = fseFactory.get();
        Fse fse = pair.getKey();
        return deserialize(fse, bytes);
    }

    public static <T> T deserialize(@Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        Pair<Fse, ByteArray> pair = fseFactory.get();
        Fse fse = pair.getKey();
        ByteArray buffer = pair.getValue();
        if(buffer!=null) {
            try {
                return deserialize(fse, buffer, bytes, targetClass);
            } finally {
                buffer.clear();
            }
        }else{
            return null;
        }
    }

    public static <T> T deserialize(@NonNull Fse fse, @Nullable byte[] bytes) {
        return deserialize(fse, null, bytes, null);
    }

    public static <T> T deserialize(@NonNull Fse fse, ByteArray buffer, @Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        if (bytes == null) {
            return null;
        }
        try {
            buffer = buffer == null ? ByteArray.allocate(bytes.length): buffer;
            buffer.clear();
            buffer.put(bytes);
            Object obj = fse.deSerialize(buffer);
            if (targetClass != null) {
                if (!Reflects.isInstance(obj, targetClass)) {
                    throw new CodecException(StringTemplates.formatWithPlaceholder("{} is not cast to {} when use FSE deserialize", Reflects.getFQNClassName(obj.getClass()), Reflects.getFQNClassName(targetClass)));
                }
                return (T) obj;
            } else {
                return (T) obj;
            }
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    static {
        Pipeline.of(CommonServiceProvider.loadService(FseCustomizer.class)).forEach(new Consumer<FseCustomizer>() {
            @Override
            public void accept(FseCustomizer fstCustomizer) {
                fseCustomizerRegistry.put(fstCustomizer.getName(), fstCustomizer);
                logger.info("Load the FST serializers for {}", fstCustomizer.getName());
            }
        });
    }

    public static void registerCustomizer(String name, FseCustomizer customizer) {
        fseCustomizerRegistry.put(name, customizer);
    }

    public static void registerCustomizer(FseCustomizer customizer) {
        fseCustomizerRegistry.put(customizer.getName(), customizer);
    }


    public static FseCustomizer getCustomizer(String name) {
        return fseCustomizerRegistry.get(name);
    }


}
