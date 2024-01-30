package com.jn.agileway.audit.core.service;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Service;
import com.jn.langx.util.function.Supplier;

public interface ServiceExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Service> {
    @Override
    Service get(AuditRequest<AuditedRequest, AuditedRequestContext> input);
}
