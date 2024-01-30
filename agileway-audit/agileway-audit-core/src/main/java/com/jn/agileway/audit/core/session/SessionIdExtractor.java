package com.jn.agileway.audit.core.session;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.langx.util.function.Supplier;

public interface SessionIdExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, String> {
}
