package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.agileway.http.rr.requestmapping.JavaMethodRequestMappingAccessorParser;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class SpringRequestMappingAccessorParser implements JavaMethodRequestMappingAccessorParser {
    @Override
    public RequestMappingAccessor parse(Method method) {
        RequestMappingAccessor accessor = null;
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation anno = RequestMappings.findFirstRequestMappingAnnotation(method);
            accessor = RequestMappings.createAccessor(anno);
        }
        return accessor;
    }
}
