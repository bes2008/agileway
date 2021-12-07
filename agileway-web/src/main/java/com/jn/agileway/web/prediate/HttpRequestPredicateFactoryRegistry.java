package com.jn.agileway.web.prediate;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class HttpRequestPredicateFactoryRegistry implements Registry<String, HttpRequestPredicateFactory> {
    private static final Logger logger = Loggers.getLogger(HttpRequestPredicateFactoryRegistry.class);
    private ConcurrentHashMap<String, HttpRequestPredicateFactory> factories = new ConcurrentHashMap<String, HttpRequestPredicateFactory>();


    private HttpRequestPredicateFactoryRegistry() {
    }

    private static final HttpRequestPredicateFactoryRegistry INSTANCE = new HttpRequestPredicateFactoryRegistry();

    public static HttpRequestPredicateFactoryRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(HttpRequestPredicateFactory factory) {
        Name name = Reflects.getAnnotation(factory.getClass(), Name.class);
        if (name != null && Objs.isNotEmpty(name.value())) {
            register(name.value(), factory);
        }
        register(factory.getName(), factory);
    }

    @Override
    public void register(String key, HttpRequestPredicateFactory factory) {
        if (factory != null) {
            if (key == null) {
                String replacement = Reflects.getFQNClassName(factory.getClass());
                logger.warn("Can't find a valid name for http request predicate factory: {}", replacement);
                key = replacement;
            }
            key = key.toLowerCase();
            factories.put(key, factory);
        }
    }

    @Override
    public HttpRequestPredicateFactory get(String name) {
        return factories.get(name.toLowerCase());
    }


    static {
        Collects.forEach(ServiceLoader.load(HttpRequestPredicateFactory.class), new Consumer<HttpRequestPredicateFactory>() {
            @Override
            public void accept(HttpRequestPredicateFactory factory) {
                INSTANCE.register(factory);
            }
        });
    }

}
