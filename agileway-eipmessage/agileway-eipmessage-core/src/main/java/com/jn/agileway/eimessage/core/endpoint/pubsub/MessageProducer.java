package com.jn.agileway.eimessage.core.endpoint.pubsub;


import com.jn.agileway.eimessage.core.channel.OutboundChannel;

public interface MessageProducer extends PubSubEndpoint {
    OutboundChannel getOutboundChannel();
    void setOutboundChannel(OutboundChannel channel);
}
