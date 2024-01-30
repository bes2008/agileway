package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.*;
import com.jn.langx.util.function.Supplier;

import java.util.List;

/**
 * 用于根据request, request context 来提取AuditEvent
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public interface AuditEventExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, AuditEvent> {

    Service extractService(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    Principal extractPrincipal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    List<Resource> extractResources(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    Operation extractOperation(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    String extractSessionId(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);
}
