package com.jn.agileway.audit.core.principal;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Principal;
import com.jn.langx.util.function.Supplier;

public interface PrincipalExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Principal> {
    @Override
    Principal get(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);
}
