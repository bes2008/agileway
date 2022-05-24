package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;

public class PipedOutboundChannel extends DefaultOutboundChannel {
    @NonNull
    private ChannelMessageInterceptorPipeline pipeline;
    @Override
    protected boolean sendInternal(Message<?> message) {
        if(pipeline!=null) {
            message = pipeline.beforeOutbound(this, message);
            if (message == null) {
                return false;
            }
            boolean sent = super.sendInternal(message);
            pipeline.afterOutbound(this, message, sent);
            return sent;
        }else{
            return super.sendInternal(message);
        }
    }

    public ChannelMessageInterceptorPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
