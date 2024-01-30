package com.jn.agileway.audit.core.operation;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Operation;
import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.langx.util.function.Supplier;

/**
 * 根据 request 提取operation definition 和 operation
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public interface OperationExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Operation> {
    /**
     * 根据request 提取 operation definition
     * @param wrappedRequest
     * @return
     */
    OperationDefinition findOperationDefinition(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);
}
