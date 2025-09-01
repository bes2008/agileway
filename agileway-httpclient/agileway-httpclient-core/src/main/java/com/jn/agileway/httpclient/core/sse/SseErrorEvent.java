package com.jn.agileway.httpclient.core.sse;

public class SseErrorEvent extends SseEvent {
    private int statusCode = -1;
    private String errorMessage;

    SseErrorEvent(SseEventSource source, int statusCode, String errorMessage) {
        super(source, SseEventType.ERROR);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
