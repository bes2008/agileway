package com.jn.agileway.codec.serialization.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.serialization.kryo.KryoCustomizer;
import com.jn.langx.util.ClassLoaders;
import de.javakaffee.kryoserializers.guava.*;

public class GuavaKryoCustomizer implements KryoCustomizer {

    @Override
    public String getName() {
        return "guava";
    }

    @Override
    public void customize(Kryo kryo) {
        if (!isGuavaLibFound()) {
            return;
        }

        // guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap, ImmutableTable, ReverseList, UnmodifiableNavigableSet
        ImmutableListSerializer.registerSerializers(kryo);
        ImmutableSetSerializer.registerSerializers(kryo);
        ImmutableMapSerializer.registerSerializers(kryo);
        ImmutableMultimapSerializer.registerSerializers(kryo);
        ImmutableTableSerializer.registerSerializers(kryo);
        ReverseListSerializer.registerSerializers(kryo);
        UnmodifiableNavigableSetSerializer.registerSerializers(kryo);

        // guava ArrayListMultimap, HashMultimap, LinkedHashMultimap, LinkedListMultimap, TreeMultimap, ArrayTable, HashBasedTable, TreeBasedTable
        ArrayListMultimapSerializer.registerSerializers(kryo);
        HashMultimapSerializer.registerSerializers(kryo);
        LinkedHashMultimapSerializer.registerSerializers(kryo);
        LinkedListMultimapSerializer.registerSerializers(kryo);
        TreeMultimapSerializer.registerSerializers(kryo);
        ArrayTableSerializer.registerSerializers(kryo);
        HashBasedTableSerializer.registerSerializers(kryo);
        TreeBasedTableSerializer.registerSerializers(kryo);

    }

    private boolean isGuavaLibFound() {
        try {
            Class clazz = ClassLoaders.loadClass("com.google.common.collect.Lists");
            return clazz == null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
