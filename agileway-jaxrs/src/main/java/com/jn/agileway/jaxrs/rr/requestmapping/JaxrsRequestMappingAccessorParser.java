package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.JavaMethodRequestMappingAccessorParser;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class JaxrsRequestMappingAccessorParser implements JavaMethodRequestMappingAccessorParser {
    @Override
    public RequestMappingAccessor parse(Method method) {
        RequestMappingAccessor accessor = null;
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation methodAnno = RequestMappings.findFirstRequestMappingAnnotation(method);

            accessor = RequestMappings.createAccessor(method, methodAnno);
        }
        return accessor;
    }
}
