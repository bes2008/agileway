package com.jn.agileway.httpclient.declarative.anno;

public @interface Cookie {
    String value() default "";

    String defaultValue() default "";
}
