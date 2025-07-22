package com.jn.agileway.httpclient.declarative.anno;

public @interface Header {
    /**
     * header name
     */
    String name() default "";

    String defaultValue() default "";

    boolean required() default true;
}
