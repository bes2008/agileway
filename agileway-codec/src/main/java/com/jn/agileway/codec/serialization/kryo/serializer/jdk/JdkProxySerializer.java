package com.jn.agileway.codec.serialization.kryo.serializer.jdk;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JdkProxySerializer extends Serializer<Object> {
    private static final Logger logger = Loggers.getLogger(JdkProxySerializer.class);

    @Override
    public Object read(final Kryo kryo, final Input input, final Class<? extends Object> type) {
        final InvocationHandler invocationHandler = (InvocationHandler) kryo.readClassAndObject(input);
        final Class<?>[] interfaces = kryo.readObject(input, Class[].class);
        final ClassLoader classLoader = kryo.getClassLoader();
        try {
            return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        } catch (final RuntimeException e) {
            logger.error("{}#read: Could not create proxy using classLoader {},  have InvocationHandler.classloader: {},  have context classloader: {}",
                    getClass().getName() , classLoader , invocationHandler.getClass().getClassLoader() ,Thread.currentThread().getContextClassLoader());
            throw e;
        }
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Object obj) {
        kryo.writeClassAndObject(output, Proxy.getInvocationHandler(obj));
        kryo.writeObject(output, obj.getClass().getInterfaces());
    }

    @Override
    public Object copy(final Kryo kryo, final Object original) {
        return Proxy.newProxyInstance(kryo.getClassLoader(), original.getClass().getInterfaces(),
                Proxy.getInvocationHandler(original));
    }
}
