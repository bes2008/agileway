package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;

public class Point2PointMessageRouter extends AbstractLifecycle implements MessageRouter {
    private OutboundChannel outboundChannel;

    public Point2PointMessageRouter(OutboundChannel outboundChannel) {
        this.outboundChannel = outboundChannel;
    }

    public void handle(Message<?> message) {
        outboundChannel.send(message);
    }
}
