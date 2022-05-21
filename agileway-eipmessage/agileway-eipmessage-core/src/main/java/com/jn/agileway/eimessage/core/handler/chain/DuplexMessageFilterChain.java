package com.jn.agileway.eimessage.core.handler.chain;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;

public interface DuplexMessageFilterChain extends MessageHandler {
    @Override
    void handle(Message<?> message);
}
