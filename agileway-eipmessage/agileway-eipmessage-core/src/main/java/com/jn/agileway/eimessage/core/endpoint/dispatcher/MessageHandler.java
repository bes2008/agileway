package com.jn.agileway.eimessage.core.endpoint.dispatcher;

import com.jn.agileway.eimessage.core.message.Message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handle(Message<?> message);
}
