package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.Nullable;

public class PipedOutboundChannel extends DefaultOutboundChannel {

    @Override
    protected boolean sendInternal(Message<?> message) {
        message = pipeline.beforeOutbound(this, message);
        if (message == null) {
            return false;
        }
        boolean sent = super.sendInternal(message);
        pipeline.afterOutbound(this, message, sent);
        return sent;
    }

    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
