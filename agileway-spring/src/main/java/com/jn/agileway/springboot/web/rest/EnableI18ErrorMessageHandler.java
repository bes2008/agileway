package com.jn.agileway.springboot.web.rest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({I18nErrorMessageHandlerConfiguration.class})
public @interface EnableI18ErrorMessageHandler {
}
