package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.spring.converter.CommonEnumByNameConverterFactory;
import com.jn.agileway.spring.web.rest.EasyjsonHttpMessageConverter;
import com.jn.agileway.spring.web.rest.GlobalSpringRestExceptionHandler;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate2;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

public class AgilewaySpringWebMvcConfigurer implements WebMvcConfigurer {

    private GlobalSpringRestExceptionHandler globalHandlerExceptionResolver;
    private EasyjsonHttpMessageConverter httpMessageConverter;

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

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CommonEnumByNameConverterFactory());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        if (httpMessageConverter != null) {
            // 参考 FormHttpConverters,
            int index =Collects.firstOccurrence(converters, new Predicate2<Integer, HttpMessageConverter<?>>() {
                public boolean test(Integer index, HttpMessageConverter<?> converter) {
                    return converter instanceof FormHttpMessageConverter;
                }
            });
            if(index<=0) {
                converters.add(httpMessageConverter);
            }else{
                converters.add(index, httpMessageConverter);
            }
        }
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }


    public void setHttpMessageConverter(EasyjsonHttpMessageConverter httpMessageConverter) {
        this.httpMessageConverter = httpMessageConverter;
    }

}
