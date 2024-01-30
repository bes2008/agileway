package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AbstractAuditEventExtractor;
import com.jn.langx.invocation.MethodInvocation;

import jakarta.servlet.http.HttpServletRequest;

public class ServletAuditEventExtractor extends AbstractAuditEventExtractor<HttpServletRequest, MethodInvocation> {
    public ServletAuditEventExtractor() {
        setPrincipalExtractor(new ServletAuditEventPrincipalExtractor());
        setServiceExtractor(new ServletAuditEventServiceExtractor());
        setSessionIdExtractor(new ServletAuditEventSessionIdExtractor());
    }
}
