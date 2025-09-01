package com.jn.agileway.httpclient.core.sse;

/**
 * SSE Message Event，要通常 Listener 交给用户使用的 Event
 */
public class SseMessageEvent extends SseEvent {
    /**
     * event 名，默认为 message
     */
    private String name = "message";
    private String data;
    private String lastEventId;
    /**
     * 重试时间，单位毫秒
     */
    private long retry;

    public SseMessageEvent(SseEventSource source, String name, String data, String lastEventId, long retry) {
        super(source, SseEventType.MESSAGE);
        this.name = name;
        this.data = data;
        this.lastEventId = lastEventId;
        this.retry = retry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public String getLastEventId() {
        return lastEventId;
    }

    public String getOrigin() {
        return getSource().getUrl();
    }

    long retry() {
        return retry;
    }
}
