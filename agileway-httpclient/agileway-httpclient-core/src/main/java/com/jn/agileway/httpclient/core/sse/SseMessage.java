package com.jn.agileway.httpclient.core.sse;

/**
 * SSE 推送的消息，这个是服务端推送给客户端的消息，只在框架内部使用
 */
public class SseMessage {
    /**
     * event 名，默认为 message
     */
    private String event = "message";
    private String data;
    private String id;
    /**
     * 重试时间，单位毫秒
     */
    private long retry;

    public String getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public long getRetry() {
        return retry;
    }
}
