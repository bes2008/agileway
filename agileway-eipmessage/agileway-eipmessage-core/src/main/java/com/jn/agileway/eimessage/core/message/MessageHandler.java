package com.jn.agileway.eimessage.core.message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handle(Message<?> message);
}
