package com.jn.agileway.httpclient.declarative.anno;

public @interface Patch {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
