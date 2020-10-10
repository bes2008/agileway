package com.jn.agileway.codec.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.kryo.KryoCustomizer;
import com.jn.agileway.codec.kryo.serializer.GregorianCalendarSerializer;
import com.jn.agileway.codec.kryo.serializer.JdkProxySerializer;

import java.lang.reflect.InvocationHandler;
import java.util.GregorianCalendar;

public class JdkClassesCustomizer implements KryoCustomizer {
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
