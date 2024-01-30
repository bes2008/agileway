package com.jn.agileway.audit.spring.boot.autoconfigure;

import com.jn.agileway.audit.core.AuditSettings;
import com.jn.langx.invocation.aop.expression.AspectJExpressionPointcutAdvisorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@ConfigurationProperties(prefix = "audit")
@ConditionalOnMissingBean(name = "auditProperties")
public class AuditProperties extends AuditSettings {
    private boolean enabled = true;
    private List<String> httpInterceptorPatterns;
    private boolean debugConsumerEnabled = true;
    @NestedConfigurationProperty
    private AspectJExpressionPointcutAdvisorProperties advisorPointcut;

    public boolean isDebugConsumerEnabled() {
        return debugConsumerEnabled;
    }

    public void setDebugConsumerEnabled(boolean debugConsumerEnabled) {
        this.debugConsumerEnabled = debugConsumerEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getHttpInterceptorPatterns() {
        return httpInterceptorPatterns;
    }

    public void setHttpInterceptorPatterns(List<String> httpInterceptorPatterns) {
        this.httpInterceptorPatterns = httpInterceptorPatterns;
    }

    public void setAdvisorPointcut(AspectJExpressionPointcutAdvisorProperties advisorPointcut) {
        this.advisorPointcut = advisorPointcut;
    }

    public AspectJExpressionPointcutAdvisorProperties getAdvisorPointcut() {
        return advisorPointcut;
    }
}
