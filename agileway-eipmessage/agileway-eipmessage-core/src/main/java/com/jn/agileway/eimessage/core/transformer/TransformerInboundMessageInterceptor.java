package com.jn.agileway.eimessage.core.transformer;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.channel.pipe.InboundMessageInterceptor;
import com.jn.agileway.eimessage.core.message.Message;

public class TransformerInboundMessageInterceptor extends InboundMessageInterceptor {
    private MessageTransformer transformer;

    @Override
    public boolean beforeInbound(InboundChannel channel) {
        return true;
    }

    @Override
    public Message<?> afterInbound(InboundChannel channel, Message<?> message) {
        if(message!=null){
            return transformer.transform(message);
        }
        return null;
    }

    public MessageTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(MessageTransformer transformer) {
        this.transformer = transformer;
    }

}
