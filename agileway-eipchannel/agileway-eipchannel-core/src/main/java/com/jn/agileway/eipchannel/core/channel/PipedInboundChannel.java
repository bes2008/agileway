package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.Nullable;


public class PipedInboundChannel extends AbstractInboundChannel {
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

    public ChannelMessageInterceptorPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }

}
