package com.jn.agileway.audit.core.operation.method;

import com.jn.agileway.audit.core.operation.OperationIdGenerator;
import com.jn.langx.invocation.MethodInvocation;

/**
 * 方法ID生成器
 * @param <AuditedRequest>
 */
public abstract class AbstractOperationMethodIdGenerator<AuditedRequest> implements OperationIdGenerator<AuditedRequest, MethodInvocation> {
    @Override
    public final String get() {
        throw new UnsupportedOperationException();
    }
}
