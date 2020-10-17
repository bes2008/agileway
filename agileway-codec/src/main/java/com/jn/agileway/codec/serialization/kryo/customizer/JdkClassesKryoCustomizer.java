package com.jn.agileway.codec.serialization.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.serialization.kryo.KryoCustomizer;
import com.jn.agileway.codec.serialization.kryo.serializer.jdk.GregorianCalendarSerializer;
import com.jn.agileway.codec.serialization.kryo.serializer.jdk.JdkProxySerializer;

import java.lang.reflect.InvocationHandler;
import java.util.GregorianCalendar;

public class JdkClassesKryoCustomizer implements KryoCustomizer {
    @Override
    public String getName() {
        return "jdk_classes";
    }

    @Override
    public void customize(Kryo kryo) {
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
    }
}
