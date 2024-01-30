package com.jn.agileway.audit.core;

import com.jn.langx.Filter;

/**
 * 对请求进行过滤，判断到底是否要进行审计
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public interface AuditRequestFilter<AuditedRequest, AuditedRequestContext> extends Filter<AuditRequest<AuditedRequest, AuditedRequestContext>> {
}
