package com.jn.agileway.audit.core.operation;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.langx.IdGenerator;

/**
 * 生成 operation id
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 *
 * @see com.jn.agileway.audit.core.operation.method.OperationMethodExtractor
 */
public interface OperationIdGenerator<AuditedRequest, AuditedRequestContext> extends IdGenerator<AuditRequest<AuditedRequest, AuditedRequestContext>> {

}
