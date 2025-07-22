package com.jn.agileway.httpclient.declarative.anno;

public @interface Get {
    String value() default "";

    String[] accept();

}
