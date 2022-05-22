package com.jn.agileway.eimessage.core.channel.pipe;


import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.channel.OutboundChannel;
import com.jn.agileway.eimessage.core.message.Message;

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
