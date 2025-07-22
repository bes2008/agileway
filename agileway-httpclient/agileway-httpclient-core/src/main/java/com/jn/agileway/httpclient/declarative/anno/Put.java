package com.jn.agileway.httpclient.declarative.anno;

public @interface Put {
    String value() default "";

    String[] accept();

    String contentType() default "";
}
