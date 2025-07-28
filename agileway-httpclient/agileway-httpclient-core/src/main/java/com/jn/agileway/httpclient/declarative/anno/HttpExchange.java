package com.jn.agileway.httpclient.declarative.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {TYPE})
public @interface HttpExchange {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
