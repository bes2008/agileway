package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.channel.InboundChannel;

public interface MessageConsumer<T> extends PubSubEndpoint {

    InboundChannel getInboundChannel();

    void setInboundChannel(InboundChannel channel);

    /**
     * 阻塞式拉取
     *
     * @return return a object
     */
    T poll();

    /**
     * 指定时间内阻塞
     *
     * @return return a object
     */
    T poll(long timeout);
}
