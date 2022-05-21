package com.jn.agileway.eimessage.core.handler;

import com.jn.agileway.eimessage.core.model.Message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handle(Message<?> message);
}
