package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.endpoint.dispatcher.MessageDispatcher;

public interface MessageConsumer<T> extends PubSubEndpoint {
    MessageDispatcher getMessageDispatcher();

    void setMessageDispatcher(MessageDispatcher dispatcher);

    InboundChannel getInboundChannel();

    void setInboundChannel(InboundChannel channel);

    /**
     * 阻塞式拉取
     * @return return a object
     */
    T poll();

    /**
     * 指定时间内阻塞
     * @return return a object
     */
    T poll(long timeout);
}
