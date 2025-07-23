package com.jn.agileway.httpclient.declarative.anno;

public @interface UriVariable {
    /**
     * The name of the URI variable to bind to.
     *
     * @return the name of the URI variable to bind to
     */
    String value() default "";
}
