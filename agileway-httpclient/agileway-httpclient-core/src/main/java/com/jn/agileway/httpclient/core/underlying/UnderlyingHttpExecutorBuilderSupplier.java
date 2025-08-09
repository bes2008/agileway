package com.jn.agileway.httpclient.core.underlying;

import com.jn.agileway.httpclient.core.HttpProtocolVersion;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.os.Platform;
import com.jn.langx.util.spi.CommonServiceProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnderlyingHttpExecutorBuilderSupplier extends AbstractInitializable implements Supplier<HttpProtocolVersion, UnderlyingHttpExecutorBuilder> {
    private Map<String, UnderlyingHttpExecutorBuilder> builders = new LinkedHashMap<String, UnderlyingHttpExecutorBuilder>();

    private static final UnderlyingHttpExecutorBuilderSupplier INSTANCE = new UnderlyingHttpExecutorBuilderSupplier();

    private UnderlyingHttpExecutorBuilderSupplier() {
        init();
    }

    public static UnderlyingHttpExecutorBuilderSupplier getInstance() {
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

    private static final List<String> recommendedBuilderNames = Lists.newArrayList("apache-httpcomponents", "okhttp3", "jdk11", "jetty");

    @Override
    public UnderlyingHttpExecutorBuilder get(HttpProtocolVersion protocolVersion) {
        // 过滤出支持该协议版本的 underlying 实现
        List<UnderlyingHttpExecutorBuilder> candidateBuilders = Pipeline.of(builders.values())
                .filter(new Predicate<UnderlyingHttpExecutorBuilder>() {
                    @Override
                    public boolean test(UnderlyingHttpExecutorBuilder builder) {
                        return builder.supportedProtocols().contains(protocolVersion) && Platform.isXXVMorGerater(builder.minJdkVersion());
                    }
                }).asList();
        if (candidateBuilders.isEmpty()) {
            throw new RuntimeException("no underlying http executor support protocol: " + protocolVersion);
        }

        List<String> candidateBuilderNames = Pipeline.of(candidateBuilders)
                .map(new Function<UnderlyingHttpExecutorBuilder, String>() {
                    @Override
                    public String apply(UnderlyingHttpExecutorBuilder builder) {
                        return builder.getName();
                    }
                }).asList();
        String name = Pipeline.of(recommendedBuilderNames)
                .findFirst(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return candidateBuilderNames.contains(s);
                    }
                });
        if (Strings.isBlank(name)) {
            name = Pipeline.of(candidateBuilderNames).findFirst();
        }
        Loggers.info(this.getClass(), "use underlying http executor builder: {}", name);
        return builders.get(name).get();
    }
}
