package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.*;
import com.jn.langx.Builder;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

public class AuditEventBuilder implements Builder<AuditEvent> {
    private AuditEvent event = new AuditEvent();

    public AuditEventBuilder principal(Principal principal) {
        event.setPrincipal(principal);
        return this;
    }

    public AuditEventBuilder operation(Operation operation) {
        event.setOperation(operation);
        return this;
    }

    public AuditEventBuilder addResource(Resource resource) {
        event.addResource(resource);
        return this;
    }

    public AuditEventBuilder addResource(Resource... resources) {
        Collects.forEach(resources, new Consumer<Resource>() {
            @Override
            public void accept(Resource resource) {
                addResource(resource);
            }
        });
        return this;
    }

    public AuditEventBuilder service(Service service) {
        event.setService(service);
        return this;
    }

    @Override
    public AuditEvent build() {
        return event;
    }
}
