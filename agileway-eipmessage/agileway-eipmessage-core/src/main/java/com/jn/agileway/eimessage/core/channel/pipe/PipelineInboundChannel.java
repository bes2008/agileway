package com.jn.agileway.eimessage.core.channel.pipe;

import com.jn.agileway.eimessage.core.channel.InboundChannel;
import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;

public class PipelineInboundChannel extends AbstractInitializable implements InboundChannel {
    @NonNull
    private InboundChannel inboundChannel;
    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;

    public PipelineInboundChannel(InboundChannel channel, ChannelMessageInterceptorPipeline pipeline) {
        this.inboundChannel = channel;
        this.pipeline = pipeline;
    }

    @Override
    public Message<?> poll() {
        return poll(-1);
    }

    @Override
    public Message<?> poll(long timeout) {
        boolean doPoll = pipeline.beforeInbound(inboundChannel);
        if (!doPoll) {
            return null;
        }
        Message<?> m = inboundChannel.poll(timeout);
        if (m != null) {
            m = pipeline.afterInbound(inboundChannel, m);
        }
        return m;
    }


    @Override
    public void setName(String s) {
        inboundChannel.setName(s);
    }

    @Override
    public String getName() {
        return inboundChannel.getName();
    }


    @Override
    public void startup() {
        inboundChannel.startup();
    }

    @Override
    public void shutdown() {
        inboundChannel.shutdown();
    }
}
