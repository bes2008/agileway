package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.langx.registry.GenericPairRegistry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.struct.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequestMappingAccessorRegistry extends GenericPairRegistry<Method, RequestMappingAccessor, Pair<Method, RequestMappingAccessor>> {
    @Override
    public Pair<Method, RequestMappingAccessor> get(Method method) {
        Preconditions.checkNotNull(method);

        Pair<Method, RequestMappingAccessor> p = super.get(method);

        if (p == null) {
            RequestMappingAccessor accessor = null;
            if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
                Annotation anno = RequestMappings.findFirstRequestMappingAnnotation(method);
                accessor = RequestMappingAccessorFactory.createAccessor(anno);
            }
            p = new Pair<Method, RequestMappingAccessor>(method, accessor);
            register(p);
        }
        return p;
    }
}
