package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.JavaMethodRequestMappingAccessorParser;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.util.reflect.Reflects;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class JaxrsRequestMappingAccessorParser implements JavaMethodRequestMappingAccessorParser {
    @Override
    public RequestMappingAccessor parse(Method method) {
        RequestMappingAccessor accessor = null;
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation methodAnno = RequestMappings.findFirstRequestMappingAnnotation(method);
            Consumes consumes = Reflects.getAnnotation(method, Consumes.class);
            Produces produces = Reflects.getAnnotation(method, Produces.class);
            Path pathAnno = Reflects.getAnnotation(method, Path.class);
            accessor = RequestMappings.createAccessor(method, methodAnno, produces, consumes, pathAnno);
        }
        return accessor;
    }
}
