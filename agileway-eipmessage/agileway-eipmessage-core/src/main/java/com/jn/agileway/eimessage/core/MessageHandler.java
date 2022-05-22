package com.jn.agileway.eimessage.core;

import com.jn.agileway.eimessage.core.Message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handle(Message<?> message);
}
