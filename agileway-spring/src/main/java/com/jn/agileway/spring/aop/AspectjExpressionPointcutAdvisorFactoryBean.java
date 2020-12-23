package com.jn.agileway.spring.aop;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class AspectjExpressionPointcutAdvisorFactoryBean implements FactoryBean<AspectJExpressionPointcutAdvisor>, InitializingBean {
    private AspectjExpressionPointcutAdvisorBuilder builder = new AspectjExpressionPointcutAdvisorBuilder();

    private AspectJExpressionPointcutAdvisor advisor;

    public void setInterceptor(Interceptor interceptor) {
        this.builder.interceptor(interceptor);
    }

    public void setExpression(String expression) {
        this.builder.expression(expression);
    }

    public void setOrder(int order) {
        this.builder.order(order);
    }

    @Override
    public AspectJExpressionPointcutAdvisor getObject() throws Exception {
        return this.advisor;
    }

    @Override
    public Class<?> getObjectType() {
        return AspectJExpressionPointcutAdvisor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.advisor = builder.build();
    }
}
