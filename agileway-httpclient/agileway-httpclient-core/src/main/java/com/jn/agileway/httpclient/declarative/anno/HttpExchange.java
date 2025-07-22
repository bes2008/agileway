package com.jn.agileway.httpclient.declarative.anno;

import com.jn.langx.util.net.http.HttpMethod;

public @interface HttpExchange {
    String value() default "";

    String[] accept();

    String contentType() default "";

    HttpMethod method() default HttpMethod.GET;
}
