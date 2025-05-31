package com.jn.agileway.httpclient.core.underlying;

import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier0;
import com.jn.langx.util.spi.CommonServiceProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnderlyingHttpRequestFactoryBuilderSupplier extends AbstractInitializable implements Supplier0<UnderlyingHttpExecutorBuilder> {
    private Map<String, UnderlyingHttpExecutorBuilder> builders = new LinkedHashMap<String, UnderlyingHttpExecutorBuilder>();

    private static final UnderlyingHttpRequestFactoryBuilderSupplier INSTANCE = new UnderlyingHttpRequestFactoryBuilderSupplier();

    private UnderlyingHttpRequestFactoryBuilderSupplier() {
        init();
    }

    public static UnderlyingHttpRequestFactoryBuilderSupplier getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doInit() throws InitializationException {
        Pipeline.of(CommonServiceProvider.loadService(UnderlyingHttpExecutorBuilder.class))
                .forEach(new Consumer<UnderlyingHttpExecutorBuilder>() {
                    @Override
                    public void accept(UnderlyingHttpExecutorBuilder builder) {
                        builders.put(builder.getName(), builder);
                    }
                });
    }

    private static final List<String> recommendedBuilderNames = Lists.newArrayList("apache-httpcomponents", "okhttp3", "jdk11");

    @Override
    public UnderlyingHttpExecutorBuilder get() {
        String name = Pipeline.of(recommendedBuilderNames)
                .findFirst(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return builders.containsKey(s);
                    }
                });
        if (Strings.isBlank(name)) {
            name = Pipeline.of(builders.keySet()).findFirst();
        }
        return builders.get(name);
    }
}
