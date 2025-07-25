package com.jn.agileway.httpclient.declarative.anno;

public @interface Cookie {
    String name() default "";

    String defaultValue() default "";
}
