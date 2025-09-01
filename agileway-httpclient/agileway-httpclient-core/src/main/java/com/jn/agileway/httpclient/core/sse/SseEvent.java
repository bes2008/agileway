package com.jn.agileway.httpclient.core.sse;

import com.jn.langx.event.DomainEvent;
import com.jn.langx.util.Objs;

public class SseEvent extends DomainEvent<SseEventSource> {
    private SseEventType type;

    public SseEvent(SseEventSource source, SseEventType type) {
        super(source.getEventDomain(), source);
        this.type = Objs.useValueIfEmpty(type, SseEventType.MESSAGE);
    }

    public SseEventType getType() {
        return type;
    }

    public static SseEvent ofOpen(SseEventSource source) {
        return new SseEvent(source, SseEventType.OPEN);
    }

}
