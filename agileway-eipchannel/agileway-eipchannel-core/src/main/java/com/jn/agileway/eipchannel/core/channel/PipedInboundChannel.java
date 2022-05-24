package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;


public class PipedInboundChannel extends DefaultInboundChannel {
    private ChannelMessageInterceptorPipeline pipeline;


    @Override
    protected Message<?> pollInternal(long timeout) {
        boolean doPoll = pipeline.beforeInbound(this);
        if (!doPoll) {
            return null;
        }
        Message<?> m = super.pollInternal(timeout);
        if (m != null) {
            m = pipeline.afterInbound(this, m);
        }
        return m;
    }

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }

}
