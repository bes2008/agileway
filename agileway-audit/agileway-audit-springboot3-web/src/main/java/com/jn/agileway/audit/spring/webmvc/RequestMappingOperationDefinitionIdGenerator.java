package com.jn.agileway.audit.spring.webmvc;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.agileway.spring.web.mvc.requestmapping.RequestMappings;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappingOperationDefinitionIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, MethodInvocation> auditRequest) {
        MethodInvocation methodInvocation = auditRequest.getRequestContext();
        Method method = methodInvocation.getJoinPoint();
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation mappingOfMethod = RequestMappings.findFirstRequestMappingAnnotation(method);
            RequestMappingAccessor<?> accessor = RequestMappings.createAccessor(mappingOfMethod);
            List<HttpMethod> httpMethods = accessor.methods();
            List<String> paths = accessor.paths();
            if (Emptys.isEmpty(paths)) {
                paths = accessor.values();
            }
            List<String> controllerPaths = SpringMvcRequestMappings.getURLTemplates(method.getDeclaringClass());


            String urlTemplate = "";
            if (Emptys.isEmpty(paths) && Emptys.isEmpty(controllerPaths)) {
                urlTemplate = "/";
            } else {
                String urlAtController = Emptys.isEmpty(controllerPaths) ? "" : controllerPaths.get(0);
                String urlAtMethod = Emptys.isEmpty(paths) ? "" : paths.get(0);
                if (Objs.isEmpty(urlAtController)) {
                    urlTemplate = urlAtMethod;
                } else {
                    urlTemplate = urlAtController;
                    if (Strings.isNotEmpty(urlAtMethod)) {
                        if (urlAtMethod.startsWith("/")) {
                            urlTemplate = urlTemplate + urlAtMethod;
                        } else {
                            urlTemplate = urlTemplate + "/" + urlAtMethod;
                        }
                    }
                }
            }
            if (Objs.length(httpMethods) != 1) {
                return urlTemplate;
            }
            return httpMethods.get(0).name() + "-" + urlTemplate;
        }
        return null;
    }
}
