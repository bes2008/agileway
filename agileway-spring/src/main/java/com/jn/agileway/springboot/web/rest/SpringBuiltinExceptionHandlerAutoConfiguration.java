package com.jn.agileway.springboot.web.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 由于 @ComponentScan 会冲突，所以在spring boot starter里，一般不会采用 @ComponentScan，
 * 以此来避免出现灵异问题（找不到期望的Bean）
 */
@Configuration
@ComponentScan("com.jn.agileway.springboot.web.rest.exceptionhandler")
public class SpringBuiltinExceptionHandlerAutoConfiguration {

}
