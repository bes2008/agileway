package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.session.SessionIdExtractor;
import com.jn.langx.invocation.MethodInvocation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class ServletAuditEventSessionIdExtractor implements SessionIdExtractor<HttpServletRequest, MethodInvocation> {
    @Override
    public String get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getId();
        }
        return null;
    }
}
