package com.jn.agileway.audit.core.resource.idresource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Resource;
import com.jn.agileway.audit.core.resource.ResourceExtractor;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> implements ResourceExtractor<AuditedRequest, AuditedRequestContext> {

    public abstract List<Serializable> findIds(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    public abstract List<E> findEntities(AuditRequest<AuditedRequest, AuditedRequestContext> request, List<Serializable> ids);

    @Override
    public List<Resource> get(final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        List<Serializable> ids = findIds(wrappedRequest);
        return findResources(wrappedRequest, ids);
    }

    public List<Resource> findResources(final AuditRequest<AuditedRequest, AuditedRequestContext> request, List<Serializable> ids) {
        List<E> entities = findEntities(request, ids);
        return Pipeline.<E>of(entities).map(new Function<E, Resource>() {
            @Override
            public Resource apply(E e) {
                return extractResource(request, e);
            }
        }).asList();
    }

    public abstract Resource extractResource(AuditRequest<AuditedRequest, AuditedRequestContext> request, E entity);
}
