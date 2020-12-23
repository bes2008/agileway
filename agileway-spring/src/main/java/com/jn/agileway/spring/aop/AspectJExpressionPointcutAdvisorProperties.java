package com.jn.agileway.spring.aop;

import com.jn.langx.annotation.NonNull;

public class AspectJExpressionPointcutAdvisorProperties {
    @NonNull
    private String expression;
    private int order;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
