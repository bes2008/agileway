package com.jn.agileway.eipchannel.core.endpoint.dispatcher;

import com.jn.agileway.eipchannel.core.message.Message;

/**
 * Base interface for any component that handles Messages.
 */
public interface MessageHandler {
    void handle(Message<?> message);
}
