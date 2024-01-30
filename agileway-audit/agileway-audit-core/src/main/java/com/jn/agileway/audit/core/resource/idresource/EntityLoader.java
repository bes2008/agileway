package com.jn.agileway.audit.core.resource.idresource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.Named;

import java.io.Serializable;
import java.util.List;

public interface EntityLoader<E> extends Named {
    List<E> load(AuditRequest request, ResourceDefinition resourceDefinition, List<Serializable> ids);
}
