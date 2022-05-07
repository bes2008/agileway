package com.jn.agileway.http.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see RestActionExceptionHandlerDefinition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestActionException {
    /**
     * 异常类，会做是否是Throwable 子类的检查
     * @return
     */
    Class value();

    /**
     * 指定被该注解标注的ExceptionHandler是否能够处理子类
     * @return
     */
    boolean supportExtends() default true;

    /**
     * 默认的状态码，当 exception handler 返回值为 null时调用
     * @return
     */
    int defaultStatusCode() default 500;

    /**
     * 默认的错误码，当 exception handler 返回值为 null时调用
     * @return
     */
    String defaultErrorCode() default "";

    /**
     * 默认的错误消息，可以是错误消息的模板，当 exception handler 返回值为 null时调用
     * @return
     */
    String defaultErrorMessage() default "";
}
