package com.jn.agileway.audit.core.operation.method;

import com.jn.agileway.audit.core.operation.OperationDefinitionParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 基于方法上的注解的解析器
 * @param <E>
 */
public interface OperationMethodAnnotationDefinitionParser<E extends Annotation> extends OperationDefinitionParser<Method> {
    Class<E> getAnnotation();
}
