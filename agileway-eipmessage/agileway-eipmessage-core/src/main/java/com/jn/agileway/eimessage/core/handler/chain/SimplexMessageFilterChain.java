package com.jn.agileway.eimessage.core.handler.chain;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;
import com.jn.langx.chain.Chain;
import com.jn.langx.chain.FilterChain;

public class SimplexMessageFilterChain implements MessageHandler {
    private Chain<Message, Object> chain;
    private boolean shared;

    public SimplexMessageFilterChain() {
        this(true);
    }

    public SimplexMessageFilterChain(boolean shared) {
        this.chain = new FilterChain<Message, Object>(shared);
    }

    public void addMessageHandler(MessageHandler handler) {
        chain.addHandler(new MessageHandlerChainAdapter(handler));
    }

    @Override
    public void handle(Message<?> message) {
        this.chain.handle(message, null);
    }
}
