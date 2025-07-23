package com.jn.agileway.httpclient.declarative.anno;

public @interface QueryParam {
    /**
     * 参数名
     *
     * @return
     */
    String value() default "";

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";
}
