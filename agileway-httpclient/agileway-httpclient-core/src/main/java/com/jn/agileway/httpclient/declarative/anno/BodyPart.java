package com.jn.agileway.httpclient.declarative.anno;

public @interface BodyPart {
    String value() default "";

    String encoding() default "UTF-8";
}
