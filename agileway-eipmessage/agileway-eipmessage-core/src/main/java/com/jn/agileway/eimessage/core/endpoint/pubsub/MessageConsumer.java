package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.endpoint.MessageDispatcher;

public interface MessageConsumer extends PubSubEndpoint {
    MessageDispatcher getMessageDispatcher();

    void setMessageDispatcher(MessageDispatcher dispatcher);

    InboundChannel getInboundChannel();

    void setInboundChannel(InboundChannel channel);
}
