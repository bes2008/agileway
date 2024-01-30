package com.jn.agileway.audit.core.model;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.CommonProps;

import java.util.List;

public class AuditEvent extends CommonProps {
    public static final long serialVersionUID = 1L;

    // who
    @NonNull
    private Principal principal; // {required}

    // when
    private long startTime;// {required} UTC time
    private long endTime;// {required} UTC time
    private long duration;  // endTime - startTime

    @Nullable
    private String sessionId;

    /**
     * service , also the main target
     */
    @NonNull
    private Service service; // {required}

    /**
     * target, also the subject target
     * maybe extract from Operation parameters
     */
    @Nullable
    private List<Resource> resources = Collects.emptyArrayList(); // {optional}

    /**
     * do what
     */
    @NonNull
    private Operation operation; // {required}


    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create()
                .prettyFormat(true)
                .serializeNulls(true)
                .build()
                .toJson(this);
    }
}
