package com.jn.agileway.httpclient.core.sse;

import com.jn.langx.event.DomainEvent;

public class SseEvent extends DomainEvent<SseEventSource> {
    private SseEventType type;

    public SseEvent(SseEventSource source, SseEventType type) {
        super("SSE", source);
        this.type = type;
    }

    public SseEventType getType() {
        return type;
    }
}
