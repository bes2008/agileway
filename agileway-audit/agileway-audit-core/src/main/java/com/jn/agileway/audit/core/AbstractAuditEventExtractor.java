package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.*;
import com.jn.agileway.audit.core.operation.OperationExtractor;
import com.jn.agileway.audit.core.principal.PrincipalExtractor;
import com.jn.agileway.audit.core.resource.ResourceExtractor;
import com.jn.agileway.audit.core.service.ServiceExtractor;
import com.jn.agileway.audit.core.session.SessionIdExtractor;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;

import java.util.Collections;
import java.util.List;

public abstract class AbstractAuditEventExtractor<AuditedRequest, AuditedRequestContext> implements AuditEventExtractor<AuditedRequest, AuditedRequestContext> {

    protected PrincipalExtractor<AuditedRequest, AuditedRequestContext> principalExtractor;
    protected ServiceExtractor<AuditedRequest, AuditedRequestContext> serviceExtractor;
    protected OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor;
    protected ResourceExtractor<AuditedRequest, AuditedRequestContext> resourceExtractor;
    protected SessionIdExtractor<AuditedRequest, AuditedRequestContext> sessionIdExtractor;

    @Override
    public AuditEvent get(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        AuditEvent event = new AuditEvent();
        wrappedRequest.setAuditEvent(event);
        event.setService(extractService(wrappedRequest));
        event.setPrincipal(extractPrincipal(wrappedRequest));
        event.setOperation(extractOperation(wrappedRequest));
        event.setResources(extractResources(wrappedRequest));
        event.setSessionId(extractSessionId(wrappedRequest));
        return event;
    }

    @Override
    public Service extractService(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        Service service = null;
        if (serviceExtractor != null) {
            service = serviceExtractor.get(wrappedRequest);
        }
        return Objs.useValueIfNull(service, new Service());
    }

    @Override
    public Principal extractPrincipal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        Principal principal = null;
        if (principalExtractor != null) {
            principal = principalExtractor.get(wrappedRequest);
        }
        return Objs.useValueIfNull(principal, new Principal());
    }

    @Override
    public List<Resource> extractResources(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        List<Resource> resources = Collections.emptyList();
        if (resourceExtractor != null) {
            List<Resource> tmp = resourceExtractor.get(wrappedRequest);
            if (Emptys.isNotEmpty(tmp)) {
                return tmp;
            }
        }
        return resources;
    }

    @Override
    public String extractSessionId(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return sessionIdExtractor == null ? null : sessionIdExtractor.get(wrappedRequest);
    }

    @Override
    public Operation extractOperation(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return operationExtractor.get(wrappedRequest);
    }

    public OperationExtractor<AuditedRequest, AuditedRequestContext> getOperationExtractor() {
        return operationExtractor;
    }

    public void setOperationExtractor(OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor) {
        this.operationExtractor = operationExtractor;
    }

    public ResourceExtractor<AuditedRequest, AuditedRequestContext> getResourceExtractor() {
        return resourceExtractor;
    }

    public void setResourceExtractor(ResourceExtractor<AuditedRequest, AuditedRequestContext> resourceExtractor) {
        this.resourceExtractor = resourceExtractor;
    }

    public PrincipalExtractor<AuditedRequest, AuditedRequestContext> getPrincipalExtractor() {
        return principalExtractor;
    }

    public void setPrincipalExtractor(PrincipalExtractor<AuditedRequest, AuditedRequestContext> principalExtractor) {
        this.principalExtractor = principalExtractor;
    }

    public ServiceExtractor<AuditedRequest, AuditedRequestContext> getServiceExtractor() {
        return serviceExtractor;
    }

    public void setServiceExtractor(ServiceExtractor<AuditedRequest, AuditedRequestContext> serviceExtractor) {
        this.serviceExtractor = serviceExtractor;
    }

    public SessionIdExtractor<AuditedRequest, AuditedRequestContext> getSessionIdExtractor() {
        return sessionIdExtractor;
    }

    public void setSessionIdExtractor(SessionIdExtractor<AuditedRequest, AuditedRequestContext> sessionIdExtractor) {
        this.sessionIdExtractor = sessionIdExtractor;
    }
}
