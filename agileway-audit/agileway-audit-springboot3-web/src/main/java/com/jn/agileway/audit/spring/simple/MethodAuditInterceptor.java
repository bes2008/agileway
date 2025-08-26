package com.jn.agileway.audit.spring.simple;

import com.jn.agileway.aop.adapter.aopalliance.MethodInvocationAdapter;
import com.jn.agileway.audit.core.auditing.aop.AuditMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

public class MethodAuditInterceptor implements MethodInterceptor {

    private AuditMethodInterceptor delegate;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return delegate.intercept(new MethodInvocationAdapter(invocation));
    }

    @Autowired
    public void setDelegate(AuditMethodInterceptor delegate) {
        this.delegate = delegate;
    }

}
