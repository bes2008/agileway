package com.jn.agileway.audit.core;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.List;

/**
 * 提供AuditRequestFilter的链式结构
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public class AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> implements AuditRequestFilter<AuditedRequest, AuditedRequestContext> {

    private List<AuditRequestFilter<AuditedRequest, AuditedRequestContext>> filters = Collects.newArrayList();

    @Override
    public boolean accept(final AuditRequest<AuditedRequest, AuditedRequestContext> auditRequest) {
        return Collects.allMatch(filters, new Predicate<AuditRequestFilter<AuditedRequest, AuditedRequestContext>>() {
            @Override
            public boolean test(AuditRequestFilter<AuditedRequest, AuditedRequestContext> filter) {
                return filter.accept(auditRequest);
            }
        });
    }

    public void addFilter(AuditRequestFilter<AuditedRequest, AuditedRequestContext> filter) {
        filters.add(filter);
    }
}
