package com.jn.agileway.audit.spring.boot.autoconfigure;

import com.jn.agileway.dmmq.core.MessageTopicDispatcher;
import com.jn.agileway.dmmq.core.consumer.DebugConsumer;
import com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorBuilder;
import com.jn.agileway.web.servlet.RRHolder;
import com.jn.agileway.audit.core.*;
import com.jn.agileway.audit.core.auditing.aop.AuditMethodInterceptor;
import com.jn.agileway.audit.core.filter.MethodInvocationAuditAnnotationFilter;
import com.jn.agileway.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.agileway.audit.core.operation.OperationIdGenerator;
import com.jn.agileway.audit.core.operation.OperationParametersExtractor;
import com.jn.agileway.audit.core.operation.method.OperationMethodInvocationExtractor;
import com.jn.agileway.audit.core.principal.PrincipalExtractor;
import com.jn.agileway.audit.core.resource.ResourceMethodInvocationExtractor;
import com.jn.agileway.audit.core.service.ServiceExtractor;
import com.jn.agileway.audit.core.session.SessionIdExtractor;
import com.jn.agileway.audit.servlet.*;
import com.jn.agileway.audit.spring.simple.MethodAuditInterceptor;
import com.jn.langx.Factory;
import com.jn.langx.exception.IllegalPropertyException;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.invocation.aop.expression.AspectJExpressionPointcutAdvisorProperties;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.function.Function2;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@ConditionalOnProperty(value = "auditor.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@Import({OperationAutoConfiguration.class, ResourceExtractAutoConfiguration.class})
@EnableConfigurationProperties(AuditProperties.class)
public class AuditAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = "servletHttpParametersExtractor")
    @Bean("servletHttpParametersExtractor")
    public ServletHttpParametersExtractor servletHttpParametersExtractor() {
        return new ServletHttpParametersExtractor();
    }

    @ConditionalOnWebApplication
    @Bean(name = "operationMethodExtractor")
    @ConditionalOnMissingBean(name = {"operationMethodExtractor"})
    public OperationMethodInvocationExtractor<HttpServletRequest> operationMethodExtractor(
            ObjectProvider<List<OperationIdGenerator<HttpServletRequest, MethodInvocation>>> operationIdGenerators,
            @Autowired @Qualifier("servletHttpParametersExtractor")
                    OperationParametersExtractor<HttpServletRequest, MethodInvocation> httpOperationParametersExtractor,
            @Autowired @Qualifier("operationDefinitionParserRegistry")
                    OperationDefinitionParserRegistry definitionParserRegistry
    ) {
        OperationMethodInvocationExtractor<HttpServletRequest> operationExtractor = new OperationMethodInvocationExtractor<HttpServletRequest>();
        operationExtractor.setOperationIdGenerators(operationIdGenerators.getObject());
        operationExtractor.setOperationParametersExtractor(httpOperationParametersExtractor);
        operationExtractor.setOperationParserRegistry(definitionParserRegistry);
        return operationExtractor;
    }


    @Bean(name = "servletAuditEventPrincipalExtractor")
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = {"servletAuditEventPrincipalExtractor"})
    public ServletAuditEventPrincipalExtractor servletAuditEventPrincipalExtractor() {
        return new ServletAuditEventPrincipalExtractor();
    }


    @Bean(name = "servletAuditEventServiceExtractor")
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = {"servletAuditEventServiceExtractor"})
    public ServletAuditEventServiceExtractor servletAuditEventServiceExtractor() {
        return new ServletAuditEventServiceExtractor();
    }

    @Bean(name = "servletAuditEventSessionIdExtractor")
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = {"servletAuditEventSessionIdExtractor"})
    public SessionIdExtractor servletAuditEventSessionIdExtractor() {
        return new ServletAuditEventSessionIdExtractor();
    }


    @Bean(name = "servletAuditEventExtractor")
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = {"servletAuditEventExtractor"})
    public ServletAuditEventExtractor servletAuditEventExtractor(
            @Qualifier("operationMethodExtractor")
                    OperationMethodInvocationExtractor<HttpServletRequest> operationMethodExtractor,

            ResourceMethodInvocationExtractor resourceMethodInvocationExtractor,
            @Qualifier("servletAuditEventServiceExtractor")
                    ServiceExtractor serviceExtractor,
            @Qualifier("servletAuditEventPrincipalExtractor")
                    PrincipalExtractor principalExtractor,
            @Qualifier("servletAuditEventSessionIdExtractor")
                    SessionIdExtractor sessionIdExtractor
    ) {
        ServletAuditEventExtractor auditEventExtractor = new ServletAuditEventExtractor();
        auditEventExtractor.setOperationExtractor(operationMethodExtractor);
        auditEventExtractor.setResourceExtractor(resourceMethodInvocationExtractor);
        auditEventExtractor.setServiceExtractor(serviceExtractor);
        auditEventExtractor.setPrincipalExtractor(principalExtractor);
        auditEventExtractor.setSessionIdExtractor(sessionIdExtractor);
        return auditEventExtractor;
    }

    @ConditionalOnWebApplication
    @Bean(name = "auditRequestFactory")
    @ConditionalOnMissingBean(name = "auditRequestFactory")
    public Function2<HttpServletRequest, MethodInvocation, ServletAuditRequest> servletAuditRequestFactory() {
        return new Function2<HttpServletRequest, MethodInvocation, ServletAuditRequest>() {
            @Override
            public ServletAuditRequest apply(HttpServletRequest request, MethodInvocation method) {
                return new ServletAuditRequest(request, method);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "debugConsumer", value = {DebugConsumer.class})
    public DebugConsumer debugConsumer() {
        return new DebugConsumer();
    }


    @Bean(name = "auditor")
    @ConditionalOnMissingBean(name = "auditor")
    @Autowired
    public Auditor auditor(AuditProperties auditSettings,
                           final MessageTopicDispatcher dispatcher,
                           AuditEventExtractor auditEventExtractor,
                           DebugConsumer debugConsumer
    ) {
        Auditor auditor = new SimpleAuditorFactory<AuditSettings>() {
            @Override
            protected Function2 getAuditRequestFactory() {
                Function2 auditRequestFactory = (Function2) applicationContext.getBean("auditRequestFactory");
                if (auditRequestFactory == null) {
                    return super.getAuditRequestFactory();
                }
                return auditRequestFactory;
            }

            @Override
            protected void initBeforeFilterChain(AuditRequestFilterChain chain, AuditSettings settings) {
                chain.addFilter(new MethodInvocationAuditAnnotationFilter<>());
            }

            @Override
            protected MessageTopicDispatcher getMessageTopicDispatcher(AuditSettings settings) {
                return dispatcher;
            }


        }.get(auditSettings);
        auditor.setAuditEventExtractor(auditEventExtractor);
        if (auditSettings.isDebugConsumerEnabled()) {
            dispatcher.subscribe("*", debugConsumer);
        }
        return auditor;
    }

    @Bean
    @ConditionalOnMissingBean(value = {MessageTopicDispatcher.class})
    public MessageTopicDispatcher messageTopicDispatcher() {
        return new MessageTopicDispatcher();
    }

    @Value("${audit.lazyFinishMode:true}")
    private boolean lazyFinishMode = false;

    @Bean
    @ConditionalOnMissingBean(AuditMethodInterceptor.class)
    @Autowired
    public AuditMethodInterceptor auditMethodInterceptor(final Auditor auditor) {
        AuditMethodInterceptor interceptor = new AuditMethodInterceptor();
        interceptor.setAuditor(auditor);
        interceptor.setLazyFinish(lazyFinishMode);
        interceptor.setThreadLocalFactory(new ThreadLocalFactory(new Factory() {
            @Override
            public Object get(Object o) {
                return RRHolder.getRequest();
            }
        }));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(MethodAuditInterceptor.class)
    @Autowired
    public MethodAuditInterceptor methodAuditInterceptor(AuditMethodInterceptor delegate) {
        MethodAuditInterceptor interceptor = new MethodAuditInterceptor();
        interceptor.setDelegate(delegate);
        return interceptor;
    }

    @Bean("auditAdvisor")
    @ConditionalOnMissingBean(name = "auditAdvisor")
    @Autowired
    public AspectJExpressionPointcutAdvisor auditAdvisor(
            @Qualifier("methodAuditInterceptor")
                    MethodAuditInterceptor interceptor,
            AuditProperties auditProperties) {

        AspectJExpressionPointcutAdvisorProperties pointcutProperties = auditProperties.getAdvisorPointcut();
        if (pointcutProperties == null) {
            throw new IllegalPropertyException(StringTemplates.formatWithPlaceholder("Illegal property: audit.advisorPointcut is not found"));
        }
        String expression = pointcutProperties.getExpression();
        if (Strings.isBlank(expression)) {
            throw new IllegalPropertyException(StringTemplates.formatWithPlaceholder("Illegal property: audit.advisorPointcut.expression, value: {}", expression));
        }

        return new AspectJExpressionPointcutAdvisorBuilder()
                .interceptor(interceptor)
                .properties(pointcutProperties)
                .build();
    }

}
