package com.jn.agileway.spring.aop;

import com.jn.langx.Builder;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.aop.expression.AspectJExpressionPointcutAdvisorProperties;
import com.jn.langx.util.Preconditions;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;

public class AspectJExpressionPointcutAdvisorBuilder implements Builder<AspectJExpressionPointcutAdvisor> {
    private Interceptor interceptor;
    private AspectJExpressionPointcutAdvisorProperties properties;

    public AspectJExpressionPointcutAdvisorBuilder interceptor(@NonNull Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public AspectJExpressionPointcutAdvisorBuilder properties(AspectJExpressionPointcutAdvisorProperties properties) {
        this.properties = properties;
        return this;
    }


    @Override
    public AspectJExpressionPointcutAdvisor build() {
        Preconditions.checkNotNull(interceptor, "Error when build a aspectj expression pointcut advisor, the interceptor is null");
        Preconditions.checkNotNull(properties, "Error when build a aspectj expression pointcut advisor, the properties is null");
        Preconditions.checkNotEmpty(properties.getExpression(), "Error when build a aspectj expression pointcut advisor, the expression is null or empty");

        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setAdvice(interceptor);
        advisor.setExpression(properties.getExpression());
        advisor.setOrder(properties.getOrder());

        return advisor;
    }
}
