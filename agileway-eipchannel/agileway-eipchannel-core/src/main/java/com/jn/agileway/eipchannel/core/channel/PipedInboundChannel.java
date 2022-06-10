package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.Nullable;


public class PipedInboundChannel extends DefaultInboundChannel {
    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;

    @Override
    protected Message<?> pollInternal(long timeout) {
        if (pipeline != null) {
            boolean doPoll = pipeline.beforeInbound(this);
            if (!doPoll) {
                return null;
            }
            Message<?> m = super.pollInternal(timeout);
            if (m != null) {
                m = pipeline.afterInbound(this, m);
            }
            return m;
        } else {
            return super.pollInternal(timeout);
        }
    }

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }

}
