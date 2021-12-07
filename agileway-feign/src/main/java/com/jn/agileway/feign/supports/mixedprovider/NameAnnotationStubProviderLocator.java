package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.langx.annotation.Name;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.util.List;
/**
 * @since 2.6.0
 */
public class NameAnnotationStubProviderLocator implements StubProviderLocator {
    private static final Logger logger = Loggers.getLogger(NameAnnotationStubProviderLocator.class);

    @Override
    public String apply(List<SimpleStubProvider> providers, Class stubClass) {
        Name name = Reflects.getAnnotation(stubClass, Name.class);
        if (name != null) {
            return name.value();
        }
        logger.warn("Please provide a @Name for restful stub class: {}", Reflects.getFQNClassName(stubClass));
        return null;
    }
}
