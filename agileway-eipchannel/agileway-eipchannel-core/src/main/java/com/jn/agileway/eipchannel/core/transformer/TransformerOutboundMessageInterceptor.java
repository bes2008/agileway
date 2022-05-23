package com.jn.agileway.eipchannel.core.transformer;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.channel.pipe.OutboundMessageInterceptor;
import com.jn.agileway.eipchannel.core.message.Message;

public class TransformerOutboundMessageInterceptor extends OutboundMessageInterceptor {
    private MessageTransformer transformer;

    @Override
    public Message<?> beforeOutbound(OutboundChannel channel, Message<?> message) {
        if (message != null) {
            return transformer.transform(message);
        }
        return null;
    }

    @Override
    public void afterOutbound(OutboundChannel channel, Message<?> message, boolean sent) {

    }

    public MessageTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(MessageTransformer transformer) {
        this.transformer = transformer;
    }
}
