package com.jn.agileway.httpclient.declarative.anno;

public @interface Delete {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
