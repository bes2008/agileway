package com.jn.agileway.audit.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 用在一个字段上、Getter、方法参数上，用于 表示被标注的 字段、参数的值就是该资源实例的名称
 * <p>
 * 被标注的应该是一个字符串 类型
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER, FIELD, METHOD})
public @interface ResourceName {
}
