package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;
import com.jn.langx.chain.Chain;
import com.jn.langx.chain.Handler;
import com.jn.langx.lifecycle.InitializationException;

public class MessageHandlerChainAdapter implements Handler<Message, Object> {
    private MessageHandler messageHandler;
    public MessageHandlerChainAdapter(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }
    @Override
    public void handle(Message request, Object response, Chain<Message, Object> chain) {
        this.messageHandler.handle(request);
        chain.handle(request, response);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() throws InitializationException {

    }
}
