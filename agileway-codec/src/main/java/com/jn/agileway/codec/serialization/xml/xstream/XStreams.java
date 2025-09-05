package com.jn.agileway.codec.serialization.xml.xstream;

import com.google.common.base.Charsets;
import com.jn.langx.Factory;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XStreams {
    private static final Map<String, XStreamCustomizer> customizers = new ConcurrentHashMap<String, XStreamCustomizer>();

    private static final List<Converter> converters = new ArrayList<Converter>();

    public static void registerCustomizer(XStreamCustomizer customizer) {
        customizers.put(customizer.getName(), customizer);
    }

    private static final ThreadLocalFactory<?, XStream> xStreamFactory = new ThreadLocalFactory<Object, XStream>(new Factory<Object, XStream>() {
        @Override
        public XStream get(Object key) {
            XStream stream = newXStream();
            stream.autodetectAnnotations(true);
            return stream;
        }
    });

    static {
        Pipeline.of(CommonServiceProvider.loadService(XStreamCustomizer.class))
                .forEach(new Consumer<XStreamCustomizer>() {
                    @Override
                    public void accept(XStreamCustomizer xStreamCustomizer) {
                        registerCustomizer(xStreamCustomizer);
                    }
                });

        Pipeline.of(CommonServiceProvider.loadService(Converter.class))
                .forEach(new Consumer<Converter>() {
                    @Override
                    public void accept(Converter converter) {
                        converters.add(converter);
                    }
                });
    }

    public static byte[] serialize(Object bean) {
        return serialize(xStreamFactory.get(), bean);
    }

    public static byte[] serialize(XStream xstream, Object bean) {
        if (bean == null) {
            return null;
        }
        return xstream.toXML(bean).getBytes(Charsets.UTF_8);
    }

    public static <T> T deserialize(XStream xStream, byte[] bytes, Class<T> targetType) {
        return (T) xStream.fromXML(new String(bytes, Charsets.UTF_8), Reflects.newInstance(targetType));
    }

    public static <T> T deserialize(byte[] bytes, Class<T> targetType) {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        return deserialize(xStreamFactory.get(), bytes, targetType);
    }

    public static XStream newXStream() {
        return newXStream(new XppDriver());
    }

    public static XStream newXStream(HierarchicalStreamDriver driver) {
        XStream xStream = new XStream(driver);
        Pipeline.of(converters)
                .forEach(new Consumer<Converter>() {
                    @Override
                    public void accept(Converter converter) {
                        xStream.registerConverter(converter);
                    }
                });
        Pipeline.of(customizers.values())
                .forEach(new Consumer<XStreamCustomizer>() {
                    @Override
                    public void accept(XStreamCustomizer xStreamCustomizer) {
                        xStreamCustomizer.customize(xStream);
                    }
                });
        return xStream;
    }

}
