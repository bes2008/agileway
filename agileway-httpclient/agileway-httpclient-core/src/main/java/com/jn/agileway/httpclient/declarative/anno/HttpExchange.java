package com.jn.agileway.httpclient.declarative.anno;

public @interface HttpExchange {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
