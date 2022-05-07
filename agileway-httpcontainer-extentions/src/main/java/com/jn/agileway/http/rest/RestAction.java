package com.jn.agileway.http.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RestAction {
    public boolean value() default true;

    /**
     * @see com.jn.langx.http.rest.RestRespBody
     */
    public String[] ignoreFields() default {};
}
