package com.jn.agileway.aop.adapter.aopalliance;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodInvocationAdapter implements com.jn.langx.invocation.MethodInvocation {
    private MethodInvocation delegate;

    public MethodInvocationAdapter(MethodInvocation methodInvocation){
        this.delegate = methodInvocation;
    }

    @Override
    public Object[] getArguments() {
        return delegate.getArguments();
    }

    @Override
    public Method getJoinPoint() {
        return delegate.getMethod();
    }

    @Override
    public Object getThis() {
        return delegate.getThis();
    }

    @Override
    public Object proceed() throws Throwable {
        return delegate.proceed();
    }
}
