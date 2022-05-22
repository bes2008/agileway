package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.channel.InboundChannel;

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

    public Object poll(long timeout){
        return null;
    }

    @Override
    public Object poll() {
        return null;
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
