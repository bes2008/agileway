package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.endpoint.MessageDispatcher;

public class DefaultMessageConsumer extends AbstractMessagePubSubEndpoint implements MessageConsumer {
    private InboundChannel inboundChannel;
    private MessageDispatcher messageDispatcher;

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public void setMessageDispatcher(MessageDispatcher dispatcher) {
        this.messageDispatcher = dispatcher;
    }

    @Override
    public InboundChannel getInboundChannel() {
        return this.inboundChannel;
    }

    @Override
    public void setInboundChannel(InboundChannel channel) {
        this.inboundChannel = channel;
    }
}
