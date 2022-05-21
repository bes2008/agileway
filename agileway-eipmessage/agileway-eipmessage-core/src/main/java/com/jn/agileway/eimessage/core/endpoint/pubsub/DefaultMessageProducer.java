package com.jn.agileway.eimessage.core.endpoint.pubsub;


import com.jn.agileway.eimessage.core.channel.OutboundChannel;

public class DefaultMessageProducer extends AbstractMessagePubSubEndpoint implements MessageProducer {
    private OutboundChannel outboundChannel;

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public OutboundChannel getOutboundChannel() {
        return this.outboundChannel;
    }

    @Override
    public void setOutboundChannel(OutboundChannel channel) {
        this.outboundChannel = channel;
    }
}
