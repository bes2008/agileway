package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.text.StringTemplates;

import jakarta.servlet.http.HttpServletRequest;

public class ServletAuditRequest extends AuditRequest<HttpServletRequest, MethodInvocation> {

    public ServletAuditRequest(HttpServletRequest request, MethodInvocation method) {
        setRequest(request);
        setRequestContext(method);
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("servletPath:{}, url:{}", getRequest().getServletPath(), getRequest().getRequestURL().toString());
    }
}
