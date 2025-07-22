package com.jn.agileway.httpclient.declarative.anno;

public @interface Post {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
