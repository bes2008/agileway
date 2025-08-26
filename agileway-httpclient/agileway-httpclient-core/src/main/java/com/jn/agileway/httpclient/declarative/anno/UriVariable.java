package com.jn.agileway.httpclient.declarative.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {PARAMETER})
public @interface UriVariable {
    /**
     * The name of the URI variable to bind to.
     *
     * @return the name of the URI variable to bind to
     */
    String value() default "";
}
