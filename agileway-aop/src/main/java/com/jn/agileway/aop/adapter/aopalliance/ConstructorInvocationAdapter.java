package com.jn.agileway.aop.adapter.aopalliance;

import org.aopalliance.intercept.ConstructorInvocation;

import java.lang.reflect.Constructor;

public class ConstructorInvocationAdapter implements com.jn.langx.invocation.ConstructorInvocation {
    private ConstructorInvocation delegate;

    public ConstructorInvocationAdapter(ConstructorInvocation delegate){
        this.delegate = delegate;
    }

    @Override
    public Object[] getArguments() {
        return delegate.getArguments();
    }

    @Override
    public Constructor getJoinPoint() {
        return delegate.getConstructor();
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
