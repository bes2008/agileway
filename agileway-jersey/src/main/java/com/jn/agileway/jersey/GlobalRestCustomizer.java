package com.jn.agileway.jersey;

import com.jn.agileway.http.rest.*;
import com.jn.agileway.http.rest.exceptionhandler.ThrowableHandler;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessorRegistry;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestExceptionHandler;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseBodyHandler;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseInterceptor;
import com.jn.agileway.jaxrs.rr.requestmapping.JaxrsRequestMappingAccessorParser;
import com.jn.agileway.jersey.exception.mapper.JerseyGlobalRestExceptionMapper;
import com.jn.agileway.jersey.validator.JerseyGlobalRestResultValidator;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.List;

public class GlobalRestCustomizer implements ResourceConfigCustomizer {
    private GlobalRestProperties restProperties;
    private final List<RestActionExceptionHandler> exceptionHandlers = Collects.emptyArrayList();


    public void setRestProperties(GlobalRestProperties restProperties) {
        this.restProperties = restProperties;
    }

    public void setExceptionHandlers(List<RestActionExceptionHandler> exceptionHandlers) {
        Collects.addAll(this.exceptionHandlers, exceptionHandlers);
    }

    @Override
    public void customize(ResourceConfig rc) {
        Preconditions.checkNotNull(restProperties);
        RequestMappingAccessorRegistry mappingAccessorRegistry = new RequestMappingAccessorRegistry();
        mappingAccessorRegistry.addParser(new JaxrsRequestMappingAccessorParser());
        mappingAccessorRegistry.init();

        GlobalRestResponseBodyHandlerProperties responseBodyHandlerProperties = restProperties.getGlobalResponseBody();
        GlobalRestExceptionHandlerProperties exceptionHandlerProperties = restProperties.getGlobalExceptionHandler();

        GlobalRestResponseBodyContext context = new GlobalRestResponseBodyContext();
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfigurationBuilder().setProperties(responseBodyHandlerProperties).build();
        context.setConfiguration(configuration);
        context.setExceptionHandlerProperties(exceptionHandlerProperties);
        context.init();

        JaxrsGlobalRestResponseBodyHandler handler = new JaxrsGlobalRestResponseBodyHandler();
        handler.setRequestMappingAccessorRegistry(mappingAccessorRegistry);
        handler.setContext(context);

        JaxrsGlobalRestResponseInterceptor interceptor = new JaxrsGlobalRestResponseInterceptor();
        interceptor.setResponseBodyHandler(handler);
        rc.register(interceptor);

        JerseyGlobalRestResultValidator validator = new JerseyGlobalRestResultValidator();
        validator.setResponseBodyHandler(handler);
        rc.register(validator);

        final GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry = new GlobalRestExceptionHandlerRegistry();
        ThrowableHandler throwableHandler = new ThrowableHandler();
        if (!Collects.contains(exceptionHandlers, throwableHandler)) {
            exceptionHandlers.add(throwableHandler);
        }
        Collects.forEach(exceptionHandlers, new Consumer<RestActionExceptionHandler>() {
            @Override
            public void accept(RestActionExceptionHandler restActionExceptionHandler) {
                exceptionHandlerRegistry.register(restActionExceptionHandler);
            }
        });
        exceptionHandlerRegistry.init();

        JaxrsGlobalRestExceptionHandler globalRestExceptionHandler = new JaxrsGlobalRestExceptionHandler();
        globalRestExceptionHandler.setContext(context);
        globalRestExceptionHandler.setExceptionHandlerRegistry(exceptionHandlerRegistry);

        JerseyGlobalRestExceptionMapper jerseyGlobalRestExceptionMapper = new JerseyGlobalRestExceptionMapper();
        jerseyGlobalRestExceptionMapper.setExceptionHandler(globalRestExceptionHandler);
        rc.register(jerseyGlobalRestExceptionMapper);

    }
}
