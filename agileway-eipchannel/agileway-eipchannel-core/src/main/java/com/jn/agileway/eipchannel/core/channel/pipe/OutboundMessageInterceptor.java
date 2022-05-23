package com.jn.agileway.eipchannel.core.channel.pipe;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;

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
