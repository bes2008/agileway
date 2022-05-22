package com.jn.agileway.eimessage.core.channel.pipe;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.message.Message;

public abstract class OutboundMessageInterceptor implements ChannelMessageInterceptor {
    @Override
    public final boolean beforeInbound(InboundChannel channel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Message<?> afterInbound(InboundChannel channel, Message<?> message) {
        throw new UnsupportedOperationException();
    }
}
