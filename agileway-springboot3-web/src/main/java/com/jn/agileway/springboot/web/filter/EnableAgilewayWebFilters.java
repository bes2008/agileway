package com.jn.agileway.springboot.web.filter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于启动全局的 Filters
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AgilewayBasicFiltersConfiguration.class})
public @interface EnableAgilewayWebFilters {

}
