package com.jn.agileway.httpclient.sse;

public enum SseEventType {
    /**
     * Fired when a connection to an event source is opened.
     */
    OPEN,
    /**
     * Fired when a connection to an event source failed to open.
     */
    ERROR,
    /**
     * Fired when a message is received from the server.
     */
    MESSAGE
}
