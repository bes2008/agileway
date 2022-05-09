package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.http.rest.*;
import com.jn.agileway.http.rr.requestmapping.JavaMethodRequestMappingAccessorParser;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessorRegistry;
import com.jn.agileway.spring.web.mvc.requestmapping.SpringRequestMappingAccessorParser;
import com.jn.agileway.spring.web.rest.EasyjsonHttpMessageConverter;
import com.jn.agileway.spring.web.rest.GlobalSpringRestExceptionHandler;
import com.jn.agileway.spring.web.rest.GlobalSpringRestResponseBodyAdvice;
import com.jn.agileway.spring.web.rest.GlobalSpringRestResponseBodyHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalFilterRestExceptionHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalFilterRestResponseHandler;
import com.jn.agileway.web.filter.globalresponse.GlobalRestResponseFilter;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

@Configuration
@Import({SpringBuiltinExceptionHandlerAutoConfiguration.class})
@AutoConfigureBefore(GlobalSpringRestResponseBodyAdvice.class)
public class GlobalRestHandlersConfiguration {

    @Bean
    public GlobalFilterRestResponseHandler filterRestResponseHandler(
            GlobalRestResponseBodyContext context
    ) {
        GlobalFilterRestResponseHandler filterRestResponseHandler = new GlobalFilterRestResponseHandler();

        filterRestResponseHandler.setContext(context);

        filterRestResponseHandler.init();
        return filterRestResponseHandler;
    }

    @Order(-90)
    @Bean
    @Autowired
    public FilterRegistrationBean globalRestFilterRegister(
            GlobalFilterRestExceptionHandler globalFilterRestExceptionHandler,
            GlobalFilterRestResponseHandler filterRestResponseHandler) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        GlobalRestResponseFilter filter = new GlobalRestResponseFilter();
        registration.setFilter(filter);
        filter.setRestResponseBodyHandler(filterRestResponseHandler);
        filter.setExceptionHandler(globalFilterRestExceptionHandler);
        registration.setUrlPatterns(Collects.newArrayList("/*"));
        registration.setName("GlobalRestResponse Filter");
        return registration;
    }

    @Bean
    @ConfigurationProperties(prefix = "agileway.rest.global-response-body")
    public GlobalRestResponseBodyHandlerProperties globalResponseBodyHandlerProperties() {
        return new GlobalRestResponseBodyHandlerProperties();
    }

    @Bean
    @ConditionalOnMissingBean({SpringRequestMappingAccessorParser.class})
    public SpringRequestMappingAccessorParser springRequestMappingAccessorParser(){
        return new SpringRequestMappingAccessorParser();
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalRestResponseBodyHandlerConfiguration.class})
    public GlobalRestResponseBodyHandlerConfiguration globalRestResponseBodyHandlerConfiguration(
            GlobalRestResponseBodyHandlerProperties properties,
            GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties
    ) {
        if (globalRestExceptionHandlerProperties.isWriteUnifiedResponse()) {
            String errorControllerClass = SpringBootErrorControllers.getErrorController();
            if (Strings.isNotBlank(errorControllerClass)) {
                properties.addAssignableType(errorControllerClass);
            }
        }
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfigurationBuilder()
                .setProperties(properties)
                .build();
        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestMappingAccessorRegistry requestMappingAccessorRegistry(ObjectProvider<List<JavaMethodRequestMappingAccessorParser>> requestMappingAccessorParserProviders) {
        final RequestMappingAccessorRegistry registry = new RequestMappingAccessorRegistry();

        Collects.forEach(requestMappingAccessorParserProviders.getIfAvailable(), new Consumer<JavaMethodRequestMappingAccessorParser>() {
            @Override
            public void accept(JavaMethodRequestMappingAccessorParser parser) {
                registry.addParser(parser);
            }
        });

        registry.init();
        return registry;
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalRestResponseBodyMapper globalRestResponseBodyMapper(
            @Autowired
                    GlobalRestResponseBodyHandlerConfiguration configuration,
            @Autowired(required = false)
            @Qualifier("globalRestResponseFieldsCleaner")
                    Predicate2<String, Object> fieldsCleaner,
            @Autowired(required = false)
            @Qualifier("globalRestResponseFieldsMapper")
                    Function<Map<String, Object>, Map<String, Object>> fieldsMapper
    ) {
        GlobalRestResponseBodyMapper responseBodyMapper = new GlobalRestResponseBodyMapper(configuration);
        if (fieldsMapper != null) {
            responseBodyMapper.setFieldsMapper(fieldsMapper);
        }
        if (fieldsCleaner != null) {
            responseBodyMapper.setFieldsCleaner(fieldsCleaner);
        }
        return responseBodyMapper;
    }

    @Bean
    @Autowired
    public GlobalRestResponseBodyContext globalRestResponseBodyContext(
            JSONFactory jsonFactory,
            GlobalRestResponseBodyHandlerConfiguration configuration,
            @Qualifier("globalRestErrorMessageHandler")
                    RestErrorMessageHandler restErrorMessageHandler,
            GlobalRestExceptionHandlerProperties exceptionHandlerProperties,
            GlobalRestResponseBodyMapper globalRestResponseBodyMapper
    ) {
        GlobalRestResponseBodyContext context = new GlobalRestResponseBodyContext();

        context.setJsonFactory(jsonFactory);

        context.setConfiguration(configuration);

        context.setExceptionHandlerProperties(exceptionHandlerProperties);
        context.setRestErrorMessageHandler(restErrorMessageHandler);

        context.setResponseBodyMapper(globalRestResponseBodyMapper);
        context.init();
        return context;
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalSpringRestResponseBodyHandler.class})
    public GlobalSpringRestResponseBodyHandler globalSpringRestResponseBodyHandler(
            GlobalRestResponseBodyContext context,
            RequestMappingAccessorRegistry requestMappingAccessorRegistry) {

        GlobalSpringRestResponseBodyHandler unifiedResponseBodyHandler = new GlobalSpringRestResponseBodyHandler();

        unifiedResponseBodyHandler.setContext(context);
        unifiedResponseBodyHandler.setRequestMappingAccessorRegistry(requestMappingAccessorRegistry);

        unifiedResponseBodyHandler.init();

        return unifiedResponseBodyHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestActionExceptionHandlerOrderFinder exceptionHandlerOrderFinder() {
        return new SpringOrderedRestExceptionHandlerOrderFinder();
    }

    @Bean
    @ConditionalOnMissingBean({GlobalRestExceptionHandlerRegistry.class})
    @Autowired
    public GlobalRestExceptionHandlerRegistry globalRestExceptionHandlerRegistry(
            RestActionExceptionHandlerOrderFinder exceptionHandlerOrderFinder
    ) {
        GlobalRestExceptionHandlerRegistry registry = new GlobalRestExceptionHandlerRegistry();
        registry.setExceptionHandlerOrderFinder(exceptionHandlerOrderFinder);
        registry.init();
        return registry;
    }

    @Autowired
    public void registerExceptionHandlers(final GlobalRestExceptionHandlerRegistry registry,
                                          @Autowired(required = false) ObjectProvider<List<RestActionExceptionHandler>> restActionExceptionHandlersProvider) {
        Collects.forEach(restActionExceptionHandlersProvider.getIfAvailable(), new Consumer<RestActionExceptionHandler>() {
            @Override
            public void accept(RestActionExceptionHandler restActionExceptionHandler) {
                registry.register(restActionExceptionHandler);
            }
        });
    }

    @Bean
    @ConfigurationProperties(prefix = "agileway.rest.global-exception-handler")
    @ConditionalOnMissingBean({GlobalRestExceptionHandlerProperties.class})
    public GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties() {
        return new GlobalRestExceptionHandlerProperties();
    }


    /**
     * Spring Controller 级别的 Rest Exception Handler
     */
    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalSpringRestExceptionHandler.class})
    public GlobalSpringRestExceptionHandler globalSpringRestExceptionHandler(
            GlobalRestExceptionHandlerRegistry registry,
            GlobalRestResponseBodyContext context
    ) {
        GlobalSpringRestExceptionHandler globalRestExceptionHandler = new GlobalSpringRestExceptionHandler();

        globalRestExceptionHandler.setContext(context);

        globalRestExceptionHandler.setExceptionHandlerRegistry(registry);
        globalRestExceptionHandler.startup();
        return globalRestExceptionHandler;
    }

    @Bean
    public EasyjsonHttpMessageConverter easyjsonHttpMessageConverter(JSONFactory jsonFactory){
        EasyjsonHttpMessageConverter converter= new EasyjsonHttpMessageConverter();
        converter.setJsonFactory(jsonFactory);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean({AgilewaySpringWebMvcConfigurer.class})
    public AgilewaySpringWebMvcConfigurer agilewaySpringWebMvcConfigurer(
            @Autowired
                    GlobalSpringRestExceptionHandler globalSpringRestExceptionHandler,
            @Autowired(required = false)
                    EasyjsonHttpMessageConverter httpMessageConverter
    ) {
        AgilewaySpringWebMvcConfigurer webMvcConfigurer = new AgilewaySpringWebMvcConfigurer();
        webMvcConfigurer.setGlobalHandlerExceptionResolver(globalSpringRestExceptionHandler);
        if (httpMessageConverter != null) {
            webMvcConfigurer.setHttpMessageConverter(httpMessageConverter);
        }
        return webMvcConfigurer;
    }

    /**
     * javax.servlet.Filter 级别的 Rest Exception Handler
     */
    @Bean
    @Autowired
    @ConditionalOnMissingBean({GlobalFilterRestExceptionHandler.class})
    public GlobalFilterRestExceptionHandler globalFilterRestExceptionHandler(
            GlobalRestResponseBodyContext context,
            GlobalRestExceptionHandlerRegistry registry) {
        GlobalFilterRestExceptionHandler globalRestExceptionHandler = new GlobalFilterRestExceptionHandler();

        globalRestExceptionHandler.setContext(context);
        globalRestExceptionHandler.setExceptionHandlerRegistry(registry);

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
