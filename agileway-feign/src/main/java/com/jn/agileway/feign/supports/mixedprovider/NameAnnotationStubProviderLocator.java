package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.RestServiceProvider;
import com.jn.langx.annotation.Name;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NameAnnotationStubProviderLocator implements RestStubProviderLocator {
    private static final Logger logger = LoggerFactory.getLogger(NameAnnotationStubProviderLocator.class);

    @Override
    public String apply(List<RestServiceProvider> providers, Class stubClass) {
        Name name = Reflects.getAnnotation(stubClass, Name.class);
        if (name != null) {
            return name.value();
        }
        logger.warn("Please provide a @Name for restful stub class: {}", Reflects.getFQNClassName(stubClass));
        return null;
    }
}
