package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Service;
import com.jn.agileway.audit.core.service.ServiceExtractor;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Strings;

import jakarta.servlet.http.HttpServletRequest;

public class ServletAuditEventServiceExtractor implements ServiceExtractor<HttpServletRequest, MethodInvocation> {
    @Override
    public Service get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Service service = new Service();
        service.setServicePort(request.getLocalPort());
        service.setServiceIp(request.getLocalAddr());
        service.setServiceProtocol(request.getProtocol());
        String context = request.getServletContext().getServletContextName();
        if (Strings.isNotEmpty(context)) {
            context = request.getContextPath();
            if (context.startsWith("/")) {
                context = context.substring(1);
            }
        }
        service.setServiceName(context);
        return service;
    }
}
