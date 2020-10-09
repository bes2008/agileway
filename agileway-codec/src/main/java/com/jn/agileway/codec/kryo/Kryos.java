package com.jn.agileway.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.jn.langx.factory.Factory;
import com.jn.langx.factory.ThreadLocalFactory;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.reflect.type.Primitives;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;

public class Kryos {
    private Kryos() {
    }

    public static final ThreadLocalFactory<?, Kryo> kryoFactory = new ThreadLocalFactory<Object, Kryo>(new Factory<Object, Kryo>() {
        @Override
        public Kryo get(Object o) {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);

            kryo.register(Arrays.asList("").getClass(), new DefaultSerializers.ArraysAsListSerializer());
            kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
            kryo.register(Collections.EMPTY_MAP.getClass(), new DefaultSerializers.CollectionsEmptyMapSerializer());
            kryo.register(Collections.EMPTY_SET.getClass(), new DefaultSerializers.CollectionsEmptySetSerializer());
            kryo.register(Collections.singletonList("").getClass(), new DefaultSerializers.CollectionsSingletonListSerializer());
            kryo.register(Collections.singleton("").getClass(), new DefaultSerializers.CollectionsSingletonSetSerializer());
            kryo.register(Collections.singletonMap("", "").getClass(), new DefaultSerializers.CollectionsSingletonMapSerializer());
            kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
            kryo.register(InvocationHandler.class, new JdkProxySerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            SynchronizedCollectionsSerializer.registerSerializers(kryo);

            // custom serializers for non-jdk libs

// register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
//            kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());

            // dexx
//            ListSerializer.registerSerializers(kryo);
//            MapSerializer.registerSerializers(kryo);
//            SetSerializer.registerSerializers(kryo);

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

    public static <T> T deserialize(byte[] bytes) {
        return deserialize(kryoFactory, bytes);
    }


    public static <T> T deserialize(Factory<?, Kryo> kryoFactory, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Input input = null;
        try {

            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input = new Input(bai);
            return (T) kryoFactory.get(null).readClassAndObject(input);
        } finally {
            IOs.close(input);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> targetType) {
        return deserialize(kryoFactory, bytes, targetType);
    }


    public static <T> T deserialize(Factory<?, Kryo> kryoFactory, byte[] bytes, Class<T> targetType) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        Preconditions.checkNotNull(targetType);
        Input input = null;
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input = new Input(bai);
            Kryo kryo = kryoFactory.get(null);
            if (!Primitives.isPrimitiveOrPrimitiveWrapperType(targetType)) {
                kryo.register(targetType, new BeanSerializer(kryo, targetType));
            }
            return (T) kryoFactory.get(null).readObjectOrNull(input, targetType);
        } finally {
            IOs.close(input);
        }
    }


}
