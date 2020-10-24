package com.jn.agileway.web.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个方法如果包含了这个注解，则是被排除的
 * 例如文件下载的请求，应该被排除的。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestActionExcluded {

}
