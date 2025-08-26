package com.jn.agileway.audit.core.auditing.aop;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.Auditor;
import com.jn.agileway.audit.core.model.OperationResult;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.invocation.aop.MethodInterceptor;
import com.jn.langx.util.concurrent.threadlocal.ThreadLocalFactory;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public class AuditMethodInterceptor<REQUEST> implements MethodInterceptor {
    private static final Logger logger = Loggers.getLogger(AuditMethodInterceptor.class);
    /**
     * 延迟finish时，不在该 Interceptor中进行 finish操作
     */
    private boolean lazyFinish = false;
    @NonNull
    private Auditor auditor;
    @NonNull
    private ThreadLocalFactory<MethodInvocation, REQUEST> threadLocalFactory;

    @Override
    public Object intercept(MethodInvocation invocation) throws Throwable {
        REQUEST request = threadLocalFactory.get(invocation);
        // 存在审计请求时，则进行审计
        if (request == null) {
            // 不存在审计请求时，则直接进行原始调用
            return invocation.proceed();
        }
        AuditRequest wrappedRequest = null;

        // start auditing
        try {
            wrappedRequest = auditor.startAudit(request, invocation);
        } catch (Throwable ex) {
            Auditor.removeByOriginalRequest(request);
            logger.error("error when the auditing starting, error: {}, request: {}", ex.getMessage(), request, ex);
        }

        if (!lazyFinish) {
            try {
                Object ret = invocation.proceed();
                return ret;
            } catch (Throwable ex) {
                if (wrappedRequest != null) {
                    wrappedRequest.setResult(OperationResult.FAIL);
                }
                throw ex;
            } finally {
                if (wrappedRequest != null) {
                    try {
                        auditor.finishAudit(wrappedRequest);
                    } catch (Throwable ex) {
                        logger.error("error when the auditing finished, error: {}, request: {}", ex.getMessage(), request, ex);
                    }
                }
            }
        } else {
            // 延迟完成时，不在该 interceptor 中进行finish操作
            return invocation.proceed();
        }
    }

    public ThreadLocalFactory<MethodInvocation, REQUEST> getThreadLocalFactory() {
        return threadLocalFactory;
    }

    public void setThreadLocalFactory(ThreadLocalFactory<MethodInvocation, REQUEST> threadLocalFactory) {
        this.threadLocalFactory = threadLocalFactory;
    }

    public Auditor getAuditor() {
        return auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public void setLazyFinish(boolean lazyFinish) {
        this.lazyFinish = lazyFinish;
    }

    public boolean isLazyFinish() {
        return lazyFinish;
    }
}
