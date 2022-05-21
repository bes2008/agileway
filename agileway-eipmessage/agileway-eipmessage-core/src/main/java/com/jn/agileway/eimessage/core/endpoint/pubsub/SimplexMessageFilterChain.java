package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;
import com.jn.langx.chain.Chain;
import com.jn.langx.chain.FilterChain;

/**
 * Simplex chain ，在pub-sub 模式下使用，producer, consumer 端都可以使用
 */
public class SimplexMessageFilterChain implements MessageFilterChain{
    private Chain<Message, Object> chain;

    public SimplexMessageFilterChain() {
        this(true);
    }

    public SimplexMessageFilterChain(boolean shared) {
        this.chain = new FilterChain<Message, Object>(shared);
    }

    public void addMessageHandler(MessageHandler handler) {
        chain.addHandler(new MessageHandlerChainAdapter(handler));
    }

    public void handle(Message<?> message) {
        this.chain.handle(message, null);
    }
}
