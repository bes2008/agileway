package com.jn.agileway.httpclient.core.sse;

import com.jn.langx.Builder;
import com.jn.langx.util.Strings;

public class SseMessageEventBuilder implements Builder<SseMessageEvent> {

    private SseEventSource source;
    private String name;
    private StringBuilder data;
    private String lastEventId;
    private long retry = -1L;

    private SseMessageEventBuilder(SseEventSource source) {
        this.source = source;
        this.data = new StringBuilder();
    }

    public static SseMessageEventBuilder newBuilder(SseEventSource source) {
        return new SseMessageEventBuilder(source);
    }

    public SseMessageEventBuilder withEventName(String name) {
        this.name = name;
        return this;
    }

    public SseMessageEventBuilder appendData(String dataFragment) {
        this.data.append(dataFragment);
        return this;
    }

    public SseMessageEventBuilder withLastEventId(String lastEventId) {
        this.lastEventId = lastEventId;
        return this;
    }

    public SseMessageEventBuilder withRetry(long retryMills) {
        this.retry = retryMills;
        return this;
    }

    @Override
    public SseMessageEvent build() {
        if (Strings.isBlank(this.data) && retry <= 0) {
            return null;
        }
        return new SseMessageEvent(this.source, this.name, this.data.toString(), this.lastEventId, this.retry);
    }
}
