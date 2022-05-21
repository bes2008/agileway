package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;

public interface MessageFilterChain {
    void addMessageHandler(MessageHandler messageHandler);
    void handle(Message<?> message);
}
