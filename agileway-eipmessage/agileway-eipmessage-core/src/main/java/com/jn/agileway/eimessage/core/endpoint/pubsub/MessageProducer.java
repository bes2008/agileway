package com.jn.agileway.eimessage.core.endpoint.pubsub;


import com.jn.agileway.eimessage.core.channel.OutboundChannel;

public interface MessageProducer<T> extends PubSubEndpoint<T> {
    OutboundChannel getOutboundChannel();
    void setOutboundChannel(OutboundChannel channel);
    boolean send(T obj);
}
