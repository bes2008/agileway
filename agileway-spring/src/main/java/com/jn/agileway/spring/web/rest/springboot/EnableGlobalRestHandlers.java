package com.jn.agileway.spring.web.rest.springboot;

import com.jn.agileway.spring.web.rest.GlobalSpringRestResponseBodyAdvice;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于启动全局的 Rest handler 处理器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({GlobalRestHandlersConfiguration.class})
@EnableAgilewayWebFilters
@ComponentScan(basePackageClasses = {
        GlobalRestHandlersConfiguration.class,
        GlobalSpringRestResponseBodyAdvice.class
})
public @interface EnableGlobalRestHandlers {
}
