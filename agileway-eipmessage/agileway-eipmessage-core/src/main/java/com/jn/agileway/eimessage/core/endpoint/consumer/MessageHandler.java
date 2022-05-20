package com.jn.agileway.eimessage.core.endpoint.consumer;

import com.jn.agileway.eimessage.core.Message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handleMessage(Message<?> message);
}
