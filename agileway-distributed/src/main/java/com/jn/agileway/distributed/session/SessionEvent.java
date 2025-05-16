package com.jn.agileway.distributed.session;

import com.jn.langx.event.DomainEvent;

public class SessionEvent extends DomainEvent<Session> {
    public enum SessionEventType {
        CREATED,
        EXPIRE,
        INVALIDATED
    }

    private SessionEventType eventType;

    public SessionEvent(String eventDomain, SessionEventType eventType, Session session) {
        super(eventDomain, session);
        this.eventType = eventType;
    }

    public SessionEventType getEventType() {
        return eventType;
    }

    public void setEventType(SessionEventType eventType) {
        this.eventType = eventType;
    }
}
