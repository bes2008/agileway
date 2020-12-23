package com.jn.agileway.spring.aop;

import com.jn.langx.Builder;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;

public class AspectJExpressionPointcutAdvisorBuilder implements Builder<AspectJExpressionPointcutAdvisor> {
    private Interceptor interceptor;
    private String expression;
    private int order;

    public AspectJExpressionPointcutAdvisorBuilder interceptor(@NonNull Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public AspectJExpressionPointcutAdvisorBuilder expression(@NonNull String expression) {
        this.expression = expression;
        return this;
    }

    public AspectJExpressionPointcutAdvisorBuilder order(int order) {
        this.order = order;
        return this;
    }


    @Override
    public AspectJExpressionPointcutAdvisor build() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        Preconditions.checkNotNull(interceptor, "Error when build a aspectj expression pointcut advisor, the interceptor is null");
        advisor.setAdvice(interceptor);

        Preconditions.checkNotEmpty(expression, "Error when build a aspectj expression pointcut advisor, the expression is null or empty");
        advisor.setExpression(expression);

        advisor.setOrder(order);

        return advisor;
    }
}
