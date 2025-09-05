package com.jn.agileway.codec.serialization.xml;

import com.jn.agileway.codec.CodecFactoryRegistry;
import com.jn.langx.codec.CodecException;
import com.jn.langx.spec.purl.PackageURLBuilder;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.langx.util.struct.Holder;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OXMs {

    private static final List<String> xmlMarshallerPackages = Lists.immutableList(
            new PackageURLBuilder().withNamespace("jakarta.xml.bind").withName("jakarta.xml.bind-api").withVersion("3.0.x-or-4.0.x").build().toPURLString(),
            new PackageURLBuilder().withNamespace("javax.xml.bind").withName("jaxb-api").withVersion("2.x").build().toPURLString(),
            new PackageURLBuilder().withNamespace("org.simpleframework").withName("simple-xml").withVersion("2.x").build().toPURLString(),
            new PackageURLBuilder().withNamespace("com.thoughtworks.xstream").withName("xstream").withVersion("1.x").build().toPURLString()
    );

    private static final List<String> oxmCodecFactories = Lists.immutableList(
            Pipeline.of(CommonServiceProvider.loadService(AbstractOXMCodecFactory.class))
                    .map(new Function<AbstractOXMCodecFactory, String>() {
                        @Override
                        public String apply(AbstractOXMCodecFactory factory) {
                            return factory.getName();
                        }
                    }).asList());

    private OXMs() {
    }

    private static final Map<Class, Holder<AbstractOXMCodec>> classToCodecCache = new ConcurrentHashMap<Class, Holder<AbstractOXMCodec>>();

    private static AbstractOXMCodec getCodec(Class expectedType) {
        if (expectedType == null) {
            return null;
        }
        Holder<AbstractOXMCodec> holder = classToCodecCache.get(expectedType);
        if (holder == null) {
            holder = new Holder<AbstractOXMCodec>();
            classToCodecCache.put(expectedType, holder);
        } else {
            return holder.get();
        }

        if (oxmCodecFactories.isEmpty()) {
            throw new CodecException("No OXM codec was registered ! Check whether the classpath contains any of the following jar files: \n" + Strings.join("\n", xmlMarshallerPackages));
        }
        String codecFactoryName = Pipeline.of(oxmCodecFactories).findFirst(new Predicate<String>() {
            @Override
            public boolean test(String name) {
                return CodecFactoryRegistry.getInstance().get(name).get(null).canSerialize(expectedType);
            }
        });
        if (Strings.isNotBlank(codecFactoryName)) {
            return (AbstractOXMCodec) CodecFactoryRegistry.getInstance().get(codecFactoryName).get(expectedType);
        } else {
            return null;
        }
    }


    public static byte[] marshal(Object javaBean) throws Exception {
        AbstractOXMCodec oxmCodec = getCodec(javaBean.getClass());
        if (oxmCodec == null) {
            throw new CodecException(StringTemplates.formatWithPlaceholder("An OXM codec for marshal class {} was not found", Reflects.getFQNClassName(javaBean.getClass())));
        }
        return oxmCodec.encode(javaBean);
    }

    public static void marshal(Object javaBean, OutputStream out) throws Exception {
        byte[] bs = marshal(javaBean);
        out.write(bs);
    }

    public static <T> T unmarshal(byte[] xml, Class<T> expectedClazz) throws Exception {
        if (Objs.isEmpty(xml)) {
            return null;
        }
        AbstractOXMCodec<T> oxmCodec = getCodec(expectedClazz);
        if (oxmCodec == null) {
            throw new CodecException(StringTemplates.formatWithPlaceholder("An OXM codec for unmarshal class {} was not found", expectedClazz));
        }
        return oxmCodec.decode(xml, expectedClazz);
    }

    public static <T> T unmarshal(String xml, Class<T> expectedClazz) throws Exception {
        if (Objs.isEmpty(xml)) {
            return null;
        }
        return unmarshal(xml.getBytes(Charsets.UTF_8), expectedClazz);
    }

    public static boolean isCanSerialize(Class clazz) {
        if (Primitives.isPrimitiveOrPrimitiveWrapperType(clazz)) {
            return false;
        }
        if (Reflects.isSubClassOrEquals(CharSequence.class, clazz)) {
            return false;
        }
        if (Reflects.isSubClassOrEquals(Date.class, clazz)) {
            return false;
        }
        return true;
    }
}
