package com.jn.agileway.codec.serialization.fst;

import com.jn.langx.Factory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class Fsts {
    private Fsts() {
    }

    private static final Logger logger = Loggers.getLogger(Fsts.class);

    private static final Map<String, FstCustomizer> fstCustomizerRegistry = new ConcurrentHashMap<String, FstCustomizer>();


    public static final ThreadLocalFactory<?, FSTConfiguration> fstFactory = new ThreadLocalFactory<Object, FSTConfiguration>(new Factory<Object, FSTConfiguration>() {
        @Override
        public FSTConfiguration get(Object o) {
            final FSTConfiguration fst = FSTConfiguration.getDefaultConfiguration();
            // 用于解决循环依赖
            fst.setShareReferences(true);
            // 不检查是否实现 Serializable, Externalizable
            fst.setStructMode(false);
            Collects.forEach(fstCustomizerRegistry, new Consumer2<String, FstCustomizer>() {
                @Override
                public void accept(String name, FstCustomizer customizer) {
                    try {
                        customizer.customize(fst);
                    } catch (Throwable ex) {
                        if (ex instanceof NoClassDefFoundError || ex instanceof ClassNotFoundException) {
                            logger.error("Error occur when register FST serializers for {}, may be you should append de.javakaffee:kryo-serializers.jar to the classpath", name);
                        } else {
                            logger.error("Error occur when register FST customizer  for {}", name);
                        }
                    }
                }
            });
            return fst;
        }
    });

    public static <T> byte[] serialize(@Nullable T o) throws IOException {
        return serialize(fstFactory, o);
    }

    public static <T> void serialize(@Nullable T o, OutputStream outputStream) throws IOException {
        serialize(fstFactory, o, outputStream);
    }

    public static <T> byte[] serialize(@NonNull Factory<?, FSTConfiguration> fstFactory, @Nullable T o) throws IOException {
        Preconditions.checkNotNull(fstFactory, "the FST factory is null");
        return serialize(fstFactory.get(null), o);
    }

    public static <T> void serialize(@NonNull Factory<?, FSTConfiguration> fstFactory, @Nullable T o, OutputStream outputStream) throws IOException {
        Preconditions.checkNotNull(fstFactory, "the FST factory is null");
        serialize(fstFactory.get(null), o, outputStream);
    }

    public static <T> byte[] serialize(@NonNull FSTConfiguration fst, @Nullable T o) throws IOException {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            serialize(fst, o, bao);
            return bao.toByteArray();
        } finally {
            IOs.close(bao);
        }
    }

    public static <T> void serialize(@NonNull FSTConfiguration fst, @NonNull T o, @NonNull OutputStream outputStream) throws IOException {
        if (o != null) {
            // 底层已经采用ThreadLocal进行了 FSTObjectOutput缓存，用于可重用，所以不必调用 output.close()
            FSTObjectOutput output = fst.getObjectOutput(outputStream);
            output.writeObject(o, o.getClass());
            output.flush();
        }
    }

    public static <T> T deserialize(@Nullable byte[] bytes) {
        return deserialize(fstFactory, bytes);
    }

    public static <T> T deserialize(@Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        return deserialize(fstFactory, bytes, targetClass);
    }

    public static <T> T deserialize(@NonNull Factory<?, FSTConfiguration> fstFactory, @Nullable byte[] bytes) {
        return deserialize(fstFactory, bytes, null);
    }

    public static <T> T deserialize(@NonNull Factory<?, FSTConfiguration> fstFactory, @Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        Preconditions.checkNotNull(fstFactory, "the FST factory is null");
        return deserialize(fstFactory.get(null), bytes, targetClass);
    }

    public static <T> T deserialize(@NonNull FSTConfiguration fst, @Nullable byte[] bytes) {
        return deserialize(fst, bytes, null);
    }

    public static <T> T deserialize(@NonNull FSTConfiguration fst, @Nullable byte[] bytes, @Nullable Class<T> targetClass) {
        if (bytes == null) {
            return null;
        }
        try {
            FSTObjectInput input = fst.getObjectInput(bytes);
            if (targetClass != null) {
                fst.registerClass(targetClass);
                return (T) input.readObject(targetClass);
            } else {
                return (T) input.readObject();
            }

        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    static {
        ServiceLoader<FstCustomizer> loader = ServiceLoader.load(FstCustomizer.class);
        Pipeline.of(loader).forEach(new Consumer<FstCustomizer>() {
            @Override
            public void accept(FstCustomizer fstCustomizer) {
                fstCustomizerRegistry.put(fstCustomizer.getName(), fstCustomizer);
                logger.info("Load the FST serializers for {}", fstCustomizer.getName());
            }
        });
    }

    public static void registerCustomizer(String name, FstCustomizer customizer) {
        fstCustomizerRegistry.put(name, customizer);
    }

    public static FstCustomizer getCustomizer(String name) {
        return fstCustomizerRegistry.get(name);
    }


}
