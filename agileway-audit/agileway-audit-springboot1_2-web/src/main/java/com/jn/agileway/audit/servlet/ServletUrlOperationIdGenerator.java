package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;

import javax.servlet.http.HttpServletRequest;

public class ServletUrlOperationIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        String method = request.getMethod();
        if (Emptys.isEmpty(method)) {
            return null;
        }
        // String context = Strings.getEmptyIfNull(request.getContextPath());
        String servletPath = Strings.getEmptyIfNull(request.getServletPath());
        StringBuilder builder = new StringBuilder(255)
                .append(request.getMethod())
                .append("-")
                .append(servletPath);
        return builder.toString();
    }

}
