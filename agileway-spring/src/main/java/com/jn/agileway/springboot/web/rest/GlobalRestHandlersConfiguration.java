package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.spring.web.rest.GlobalSpringRestExceptionHandler;
import com.jn.agileway.spring.web.rest.GlobalSpringRestResponseBodyAdvice;
import com.jn.agileway.spring.web.rest.GlobalSpringRestResponseBodyHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalFilterRestExceptionHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalFilterRestResponseHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalRestResponseFilter;
import com.jn.agileway.web.rest.*;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.util.collection.Collects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@Import({SpringBuiltinExceptionHandlerAutoConfiguration.class})
@AutoConfigureBefore(GlobalSpringRestResponseBodyAdvice.class)
public class GlobalRestHandlersConfiguration {
    @Order(-100)
    @Bean
    @Autowired
    public FilterRegistrationBean globalRestFilterRegister(
            GlobalFilterRestExceptionHandler globalFilterRestExceptionHandler,
            JSONFactory jsonFactory) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        GlobalRestResponseFilter filter = new GlobalRestResponseFilter();
        registration.setFilter(filter);
        GlobalFilterRestResponseHandler filterRestResponseHandler = new GlobalFilterRestResponseHandler();
        filterRestResponseHandler.setJsonFactory(jsonFactory);
        filter.setRestResponseBodyHandler(filterRestResponseHandler);
        filter.setExceptionHandler(globalFilterRestExceptionHandler);
        registration.setUrlPatterns(Collects.newArrayList("/api/v1/*"));
        return registration;
    }

    @Bean
    @ConfigurationProperties(prefix = "agileway.rest.global-response-body")
    public GlobalRestResponseBodyHandlerProperties globalResponseBodyHandlerProperties() {
        return new GlobalRestResponseBodyHandlerProperties();
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalSpringRestResponseBodyHandler.class})
    public GlobalSpringRestResponseBodyHandler globalSpringRestResponseBodyHandler(
            GlobalRestResponseBodyHandlerProperties properties,
            JSONFactory jsonFactory) {
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfigurationBuilder().setProperties(properties).build();
        GlobalSpringRestResponseBodyHandler unifiedResponseBodyHandler = new GlobalSpringRestResponseBodyHandler();
        unifiedResponseBodyHandler.setJsonFactory(jsonFactory);
        unifiedResponseBodyHandler.setConfiguration(configuration);
        return unifiedResponseBodyHandler;
    }

    @Bean
    @ConditionalOnMissingBean({GlobalRestExceptionHandlerRegistry.class})
    public GlobalRestExceptionHandlerRegistry globalRestExceptionHandlerRegistry() {
        GlobalRestExceptionHandlerRegistry registry = new GlobalRestExceptionHandlerRegistry();
        registry.init();
        return registry;
    }

    @Autowired
    public void registerExceptionHandlers(GlobalRestExceptionHandlerRegistry registry, ObjectProvider<List<RestActionExceptionHandler>> restActionExceptionHandlersProvider) {
        /**
         restActionExceptionHandlersProvider.stream().forEach(new Consumer<RestActionExceptionHandler>() {
        @Override public void accept(RestActionExceptionHandler restActionExceptionHandler) {
        registry.register(restActionExceptionHandler);
        }
        });
         */
        restActionExceptionHandlersProvider.getObject().forEach(new Consumer<RestActionExceptionHandler>() {
            @Override
            public void accept(RestActionExceptionHandler restActionExceptionHandler) {
                registry.register(restActionExceptionHandler);
            }
        });
    }

    @Bean
    @ConditionalOnMissingBean({RestErrorMessageHandler.class})
    public RestErrorMessageHandler errorMessageHandler() {
        return NoopRestErrorMessageHandler.INSTANCE;
    }

    @Bean
    @ConfigurationProperties(prefix = "agileway.rest.global-exception-handler")
    @ConditionalOnMissingBean({GlobalRestExceptionHandlerProperties.class})
    public GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties() {
        return new GlobalRestExceptionHandlerProperties();
    }

    /**
     * Spring Controller 级别的 Rest Exception Handler
     *
     * @param jsonFactory
     * @param registry
     * @param globalRestExceptionHandlerProperties
     * @return
     */
    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalSpringRestExceptionHandler.class})
    public GlobalSpringRestExceptionHandler globalSpringRestExceptionHandler(
            JSONFactory jsonFactory,
            GlobalRestExceptionHandlerRegistry registry,
            GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties,
            RestErrorMessageHandler restErrorMessageHandler) {
        GlobalSpringRestExceptionHandler globalRestExceptionHandler = new GlobalSpringRestExceptionHandler();

        globalRestExceptionHandler.setDefaultErrorCode(globalRestExceptionHandlerProperties.getDefaultErrorCode());
        globalRestExceptionHandler.setDefaultErrorMessage(globalRestExceptionHandlerProperties.getDefaultErrorMessage());
        globalRestExceptionHandler.setDefaultErrorStatusCode(globalRestExceptionHandlerProperties.getDefaultErrorStatusCode());
        globalRestExceptionHandler.setCauseScanEnabled(globalRestExceptionHandlerProperties.isCauseScanEnabled());
        globalRestExceptionHandler.setWriteUnifiedResponse(globalRestExceptionHandlerProperties.isWriteUnifiedResponse());
        globalRestExceptionHandler.setErrorMessageHandler(restErrorMessageHandler);

        globalRestExceptionHandler.setJsonFactory(jsonFactory);
        globalRestExceptionHandler.setExceptionHandlerRegistry(registry);
        globalRestExceptionHandler.startup();
        return globalRestExceptionHandler;
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean({AgilewaySpringWebMvcConfigurer.class})
    public AgilewaySpringWebMvcConfigurer agilewaySpringWebMvcConfigurer(GlobalSpringRestExceptionHandler globalSpringRestExceptionHandler) {
        AgilewaySpringWebMvcConfigurer webMvcConfigurer = new AgilewaySpringWebMvcConfigurer();
        webMvcConfigurer.setGlobalHandlerExceptionResolver(globalSpringRestExceptionHandler);
        return webMvcConfigurer;
    }

    /**
     * javax.servlet.Filter 级别的 Rest Exception Handler
     *
     * @param jsonFactory
     * @param registry
     * @param globalRestExceptionHandlerProperties
     * @return
     */
    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalFilterRestExceptionHandler.class})
    public GlobalFilterRestExceptionHandler globalFilterRestExceptionHandler(
            JSONFactory jsonFactory,
            GlobalRestExceptionHandlerRegistry registry,
            GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties,
            RestErrorMessageHandler restErrorMessageHandler) {
        GlobalFilterRestExceptionHandler globalRestExceptionHandler = new GlobalFilterRestExceptionHandler();

        globalRestExceptionHandler.setDefaultErrorCode(globalRestExceptionHandlerProperties.getDefaultErrorCode());
        globalRestExceptionHandler.setDefaultErrorMessage(globalRestExceptionHandlerProperties.getDefaultErrorMessage());
        globalRestExceptionHandler.setDefaultErrorStatusCode(globalRestExceptionHandlerProperties.getDefaultErrorStatusCode());
        globalRestExceptionHandler.setCauseScanEnabled(globalRestExceptionHandlerProperties.isCauseScanEnabled());
        globalRestExceptionHandler.setWriteUnifiedResponse(globalRestExceptionHandlerProperties.isWriteUnifiedResponse());

        globalRestExceptionHandler.setJsonFactory(jsonFactory);
        globalRestExceptionHandler.setExceptionHandlerRegistry(registry);
        globalRestExceptionHandler.setErrorMessageHandler(restErrorMessageHandler);

        globalRestExceptionHandler.startup();
        return globalRestExceptionHandler;
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalSpringRestResponseBodyAdvice.class})
    public GlobalSpringRestResponseBodyAdvice globalSpringRestResponseBodyAdvice(GlobalSpringRestResponseBodyHandler globalSpringRestResponseBodyHandler) {
        GlobalSpringRestResponseBodyAdvice globalSpringRestResponseBodyAdvice = new GlobalSpringRestResponseBodyAdvice();
        globalSpringRestResponseBodyAdvice.setResponseBodyHandler(globalSpringRestResponseBodyHandler);
        return globalSpringRestResponseBodyAdvice;
    }
}
