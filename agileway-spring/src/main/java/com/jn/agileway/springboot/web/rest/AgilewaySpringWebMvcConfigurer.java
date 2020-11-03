package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.spring.web.rest.GlobalSpringRestExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class AgilewaySpringWebMvcConfigurer implements WebMvcConfigurer {

    private GlobalSpringRestExceptionHandler globalHandlerExceptionResolver;

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        if (globalHandlerExceptionResolver != null) {
            resolvers.add(globalHandlerExceptionResolver);
        }
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {

    }

    public void setGlobalHandlerExceptionResolver(GlobalSpringRestExceptionHandler globalHandlerExceptionResolver) {
        this.globalHandlerExceptionResolver = globalHandlerExceptionResolver;
    }
}
