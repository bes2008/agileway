package com.jn.agileway.audit.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * A mark annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, PACKAGE})
public @interface Audit {
    /**
     * @return whether enable audit for a method
     */
    boolean enable() default true;
}
