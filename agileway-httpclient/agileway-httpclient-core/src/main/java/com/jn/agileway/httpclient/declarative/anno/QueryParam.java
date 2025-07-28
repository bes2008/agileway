package com.jn.agileway.httpclient.declarative.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {PARAMETER})
public @interface QueryParam {
    /**
     * 参数名
     *
     * @return
     */
    String value() default "";

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";
}
