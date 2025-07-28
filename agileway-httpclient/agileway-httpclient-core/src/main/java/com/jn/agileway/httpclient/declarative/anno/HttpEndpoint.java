package com.jn.agileway.httpclient.declarative.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {TYPE})
public @interface HttpEndpoint {
    /**
     * 端点的url
     */
    String value() default "";

    String[] accept() default {};

    String contentType() default "";
}
