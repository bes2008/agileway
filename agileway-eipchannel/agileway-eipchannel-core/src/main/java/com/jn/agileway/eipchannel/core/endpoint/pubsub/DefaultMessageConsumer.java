package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;

public class DefaultMessageConsumer extends AbstractMessagePubSubEndpoint implements MessageConsumer {
    private InboundChannel inboundChannel;

    @Override
    protected void doStart() {
        inboundChannel.startup();
    }

    @Override
    protected void doStop() {
        inboundChannel.shutdown();
    }

    public Object poll(long timeout) {
        return this.inboundChannel.poll(timeout);
    }

    @Override
    public Object poll() {
        return poll(-1);
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
