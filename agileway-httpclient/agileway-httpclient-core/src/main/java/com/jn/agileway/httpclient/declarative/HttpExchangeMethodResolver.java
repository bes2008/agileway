package com.jn.agileway.httpclient.declarative;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface HttpExchangeMethodResolver {
    HttpExchangeMethod resolve(Method method);

    Class<? extends Annotation> endpointAnnotation();
    
    Class<? extends Annotation>[] requiredMethodAnnotations();
}
