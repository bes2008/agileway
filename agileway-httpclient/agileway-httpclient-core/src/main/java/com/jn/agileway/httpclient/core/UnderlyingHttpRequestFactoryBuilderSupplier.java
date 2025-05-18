package com.jn.agileway.httpclient.core;

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

public class UnderlyingHttpRequestFactoryBuilderSupplier extends AbstractInitializable implements Supplier0<UnderlyingHttpRequestFactoryBuilder> {
    private Map<String, UnderlyingHttpRequestFactoryBuilder> builders = new LinkedHashMap<String, UnderlyingHttpRequestFactoryBuilder>();

    public UnderlyingHttpRequestFactoryBuilderSupplier() {
        init();
    }

    @Override
    protected void doInit() throws InitializationException {
        Pipeline.of(CommonServiceProvider.loadService(UnderlyingHttpRequestFactoryBuilder.class))
                .forEach(new Consumer<UnderlyingHttpRequestFactoryBuilder>() {
                    @Override
                    public void accept(UnderlyingHttpRequestFactoryBuilder builder) {
                        builders.put(builder.getName(), builder);
                    }
                });
    }

    private static final List<String> recommendedBuilderNames = Lists.newArrayList("apache-httpcomponents", "okhttp3", "jdk11");

    @Override
    public UnderlyingHttpRequestFactoryBuilder get() {
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
