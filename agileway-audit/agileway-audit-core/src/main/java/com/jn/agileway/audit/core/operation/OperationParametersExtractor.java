package com.jn.agileway.audit.core.operation;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.langx.util.function.Supplier;

import java.util.Map;

/**
 * 请求参数提取器
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public interface OperationParametersExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Map<String, Object>> {


}
