package com.jn.agileway.eipchannel.core.channel.pipe;


import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;

public abstract class InboundMessageInterceptor implements ChannelMessageInterceptor {
    @Override
    public final Message<?> beforeOutbound(OutboundChannel channel, Message<?> message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void afterOutbound(OutboundChannel channel, Message<?> message, boolean sent) {
        throw new UnsupportedOperationException();
    }
}
