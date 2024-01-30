package com.jn.agileway.audit.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * The operation definition annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface Operation {
    String code() default "";    //  class fullname +"."+methodName

    String name() default "";    //

    String description() default ""; //

    String type() default ""; //

    String module() default "";

    Resource resourceDefinition() default @Resource();
}
