package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.endpoint.dispatch.MessageDispatcher;
import com.jn.agileway.eimessage.core.message.Message;

public class DefaultOutboundChannel extends AbstractOutboundChannel{
    private MessageDispatcher dispatcher;

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        return this.dispatcher.dispatch(message);
    }
}
