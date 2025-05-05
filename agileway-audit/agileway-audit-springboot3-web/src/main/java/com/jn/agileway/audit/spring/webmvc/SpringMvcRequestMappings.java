package com.jn.agileway.audit.spring.webmvc;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.agileway.spring.web.mvc.requestmapping.RequestMappings;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class SpringMvcRequestMappings {

    public static List<String> getURLTemplates(Class clazz) {
        RequestMapping mapping = RequestMappings.getRequestMapping(clazz);
        if (mapping == null) {
            return Collects.newArrayList();
        }
        return getURLTemplates(mapping);
    }

    public static List<String> getURLTemplates(Method method) {
        RequestMapping mapping = RequestMappings.getRequestMapping(method);
        if (mapping == null) {
            return Collects.newArrayList();
        }
        return getURLTemplates(mapping);
    }

    public static List<String> getURLTemplates(@NonNull Annotation requestMapping) {
        RequestMappingAccessor<?> accessor = Preconditions.checkNotNull(RequestMappings.createAccessor(requestMapping));
        List<String> urls = accessor.paths();
        if (Objs.isEmpty(urls)) {
            return accessor.values();
        } else {
            return urls;
        }
    }

}
