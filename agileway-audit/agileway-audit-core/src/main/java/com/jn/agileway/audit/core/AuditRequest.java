package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.AuditEvent;
import com.jn.agileway.audit.core.model.OperationResult;

/**
 * 对真实请求的封装。
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public class AuditRequest<AuditedRequest, AuditedRequestContext> {
    /**
     * 某个请求的对应的event
     */
    private AuditEvent auditEvent;
    /**
     * 审计请求，可以HttpRequest，也可以是方法调用
     */
    private AuditedRequest request;
    /**
     * 请求上下文
     */
    private AuditedRequestContext requestContext;
    /**
     * 控制该Request是否需要审计
     */
    private boolean auditIt = true;
    /**
     * 请求开始时间，单位 mills
     */
    private long startTime;
    /**
     * 请求结束时间，单位 mills
     */
    private long endTime;
    /**
     * 该请求对应的event 最终会发到哪个topic里
     */
    private String topic;

    /**
     * 请求执行的结果
     */
    private OperationResult result;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public AuditEvent getAuditEvent() {
        return auditEvent;
    }

    public void setAuditEvent(AuditEvent auditEvent) {
        this.auditEvent = auditEvent;
    }

    public AuditedRequest getRequest() {
        return request;
    }

    public void setRequest(AuditedRequest request) {
        this.request = request;
    }

    public boolean isAuditIt() {
        return auditIt;
    }

    public void setAuditIt(boolean auditIt) {
        this.auditIt = auditIt;
    }

    public AuditedRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(AuditedRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return requestContext != null ? requestContext.toString() : (request != null ? request.toString() : "audit it");
    }
}
