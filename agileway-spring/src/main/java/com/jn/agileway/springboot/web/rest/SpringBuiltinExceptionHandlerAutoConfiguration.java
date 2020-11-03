package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.springboot.web.rest.exceptionhandler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 由于 @ComponentScan 会冲突，所以在spring boot starter里，一般不会采用 @ComponentScan，
 * 以此来避免出现灵异问题（找不到期望的Bean）
 */
@Configuration
public class SpringBuiltinExceptionHandlerAutoConfiguration {
    @Bean
    public AsyncRequestTimeoutExceptionHandler asyncRequestTimeoutExceptionHandler() {
        return new AsyncRequestTimeoutExceptionHandler();
    }

    @Bean
    public Http400ExceptionHandler http400ExceptionHandler() {
        return new Http400ExceptionHandler();
    }

    @Bean
    public Http404ExceptionHandler http404ExceptionHandler() {
        return new Http404ExceptionHandler();
    }

    @Bean
    public HttpMediaTypeNotAcceptableExceptionHandler httpMediaTypeNotAcceptableExceptionHandler() {
        return new HttpMediaTypeNotAcceptableExceptionHandler();
    }

    @Bean
    public HttpMediaTypeNotSupportedExceptionHandler httpMediaTypeNotSupportedExceptionHandler() {
        return new HttpMediaTypeNotSupportedExceptionHandler();
    }

    @Bean
    public HttpRequestMethodNotSupportedExceptionHandler httpRequestMethodNotSupportedExceptionHandler() {
        return new HttpRequestMethodNotSupportedExceptionHandler();
    }

    @Bean
    public SpringServerExceptionHandler springServerExceptionHandler() {
        return new SpringServerExceptionHandler();
    }

}
