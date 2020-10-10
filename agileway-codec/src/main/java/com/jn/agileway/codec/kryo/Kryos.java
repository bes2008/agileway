package com.jn.agileway.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.factory.Factory;
import com.jn.langx.factory.ThreadLocalFactory;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class Kryos {

    private Kryos() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Kryos.class);

    private static final Map<String, KryoCustomizer> kryoCustomizerRegistry = new ConcurrentHashMap<String, KryoCustomizer>();

    public static final ThreadLocalFactory<?, Kryo> kryoFactory = new ThreadLocalFactory<Object, Kryo>(new Factory<Object, Kryo>() {
        @Override
        public Kryo get(Object o) {
            final Kryo kryo = new Kryo();

            kryo.setOptimizedGenerics(true);

            kryo.setReferences(true);
            kryo.setCopyReferences(false);
            // 设置为 false，等价于禁用了精确的类 serializer 查找，例如 默认有 Map接口的 Serializer，没有HashMap的，
            // 如果类是个hashMap，如果设置为true，即精确查找的话，会因为找不到合适的Serializer而导致序列化失败，
            kryo.setRegistrationRequired(false);
            Collects.forEach(kryoCustomizerRegistry, new Consumer2<String, KryoCustomizer>() {
                @Override
                public void accept(String name, KryoCustomizer customizer) {
                    try {
                        customizer.customize(kryo);
                    } catch (Throwable ex) {
                        logger.error("Error occur when use the {} kry customizer : {}", name, Reflects.getFQNClassName(customizer.getClass()));
                    }
                }
            });


// joda DateTime, LocalDate, LocalDateTime and LocalTime
//            kryo.register(DateTime.class, new JodaDateTimeSerializer());
//            kryo.register(LocalDate.class, new JodaLocalDateSerializer());
//            kryo.register(LocalDateTime.class, new JodaLocalDateTimeSerializer());
//            kryo.register(LocalDateTime.class, new JodaLocalTimeSerializer());
// protobuf
//            kryo.register(SampleProtoA.class, new ProtobufSerializer()); // or override Kryo.getDefaultSerializer as shown below
// wicket
//            kryo.register( MiniMap.class, new MiniMapSerializer() );
// guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap, ImmutableTable, ReverseList, UnmodifiableNavigableSet
//            ImmutableListSerializer.registerSerializers(kryo);
//            ImmutableSetSerializer.registerSerializers(kryo);
//            ImmutableMapSerializer.registerSerializers(kryo);
//            ImmutableMultimapSerializer.registerSerializers(kryo);
//            ImmutableTableSerializer.registerSerializers(kryo);
//            ReverseListSerializer.registerSerializers(kryo);
//            UnmodifiableNavigableSetSerializer.registerSerializers(kryo);
// guava ArrayListMultimap, HashMultimap, LinkedHashMultimap, LinkedListMultimap, TreeMultimap, ArrayTable, HashBasedTable, TreeBasedTable
//            ArrayListMultimapSerializer.registerSerializers(kryo);
//            HashMultimapSerializer.registerSerializers(kryo);
//            LinkedHashMultimapSerializer.registerSerializers(kryo);
//            LinkedListMultimapSerializer.registerSerializers(kryo);
//            TreeMultimapSerializer.registerSerializers(kryo);
//            ArrayTableSerializer.registerSerializers(kryo);
//            HashBasedTableSerializer.registerSerializers(kryo);
//            TreeBasedTableSerializer.registerSerializers(kryo);
            return kryo;
        }
    });

    public static <T> byte[] serialize(T o) throws IOException {
        return serialize(kryoFactory, o);
    }

    public static <T> byte[] serialize(Factory<?, Kryo> kryoFactory, T o) {
        if (o == null) {
            return null;
        }
        Kryo kryo = kryoFactory.get(null);
        return serialize(kryo, o);
    }

    public static <T> byte[] serialize(Kryo kryo, T o) {
        Output output = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            output = new Output(bao);
            if (!Primitives.isPrimitiveOrPrimitiveWrapperType(o.getClass())) {
                kryo.register(o.getClass(), new BeanSerializer(kryo, o.getClass()));
            }
            kryo.writeClassAndObject(output, o);
            output.flush();
            return bao.toByteArray();
        } finally {
            IOs.close(output);
        }
    }

    public static <T> T deserialize(byte[] bytes) throws IOException {
        return deserialize(kryoFactory, bytes);
    }

    public static <T> T deserialize(@Nullable byte[] bytes, @Nullable Class<T> targetType) throws IOException {
        return deserialize(getKryoFactory(null), bytes, targetType);
    }

    public static <T> T deserialize(Factory<?, Kryo> kryoFactory, byte[] bytes) throws IOException {
        return deserialize(getKryoFactory(kryoFactory), bytes, null);
    }


    public static <T> T deserialize(@Nullable Factory<?, Kryo> kryoFactory, @Nullable byte[] bytes, @Nullable Class<T> targetType) throws IOException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return deserialize(getKryoFactory(kryoFactory).get(null), bytes, targetType);
    }


    public static <T> T deserialize(Kryo kryo, byte[] bytes) throws IOException {
        return deserialize(kryo, bytes, null);
    }


    public static <T> T deserialize(Kryo kryo, byte[] bytes, Class<T> targetType) throws IOException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        Input input = null;
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input = new Input(bai);
            if (targetType != null) {
                autoRegister(kryo, targetType);
            }
            Class actualClass = null;

            // 读类
            Registration actualTypeRegistration = kryo.readClass(input);

            if (input.available() < 1) {
                return null;
            }

            if (actualTypeRegistration != null) {
                actualClass = actualTypeRegistration.getType();
            }
            // 读数据
            if (actualClass != null && targetType != null) {
                if (Reflects.isSubClassOrEquals(targetType, actualClass)) {
                    if (actualClass != targetType) {
                        autoRegister(kryo, actualClass);
                    }
                    return (T) kryo.readObjectOrNull(input, actualClass);
                } else {
                    throw new ClassCastException(StringTemplates.formatWithPlaceholder("class {} is not cast to {}", Reflects.getFQNClassName(actualClass), Reflects.getFQNClassName(targetType)));
                }
            } else {
                if (targetType == null) {
                    return (T) kryo.readObjectOrNull(input, actualClass);
                } else {
                    return kryo.readObjectOrNull(input, targetType);
                }
            }
        } finally {
            IOs.close(input);
        }
    }

    public static Factory<?, Kryo> getKryoFactory(@Nullable Factory<?, Kryo> kryoFactory) {
        return kryoFactory == null ? Kryos.kryoFactory : kryoFactory;
    }

    public static void autoRegister(Kryo kryo, Class type) {
        if (!Primitives.isPrimitiveOrPrimitiveWrapperType(type)) {
            if ((kryo.getRegistration(type) != null && kryo.getSerializer(type) != null) || kryo.getDefaultSerializer(type) != null) {
                kryo.register(type, new BeanSerializer(kryo, type));
            }
        }
    }

    static {
        ServiceLoader<KryoCustomizer> loader = ServiceLoader.load(KryoCustomizer.class);
        Pipeline.of(loader).forEach(new Consumer<KryoCustomizer>() {
            @Override
            public void accept(KryoCustomizer kryoCustomizer) {
                kryoCustomizerRegistry.put(kryoCustomizer.getName(), kryoCustomizer);
            }
        });
    }

    public static void registerCustomizer(String name, KryoCustomizer customizer) {
        kryoCustomizerRegistry.put(name, customizer);
    }

    public static KryoCustomizer getCustomizer(String name) {
        return kryoCustomizerRegistry.get(name);
    }
}
