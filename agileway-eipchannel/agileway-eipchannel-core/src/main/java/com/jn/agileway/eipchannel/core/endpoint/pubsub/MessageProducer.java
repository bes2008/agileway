package com.jn.agileway.eipchannel.core.endpoint.pubsub;


import com.jn.agileway.eipchannel.core.channel.OutboundChannel;

public interface MessageProducer<T> extends PubSubEndpoint<T> {
    OutboundChannel getOutboundChannel();

    void setOutboundChannel(OutboundChannel channel);

    boolean send(T obj);
}
