package com.jn.agileway.http.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestActionExceptions {
    String name() default "";
    RestActionException[] value() default {};
    int order() default 0;
}
