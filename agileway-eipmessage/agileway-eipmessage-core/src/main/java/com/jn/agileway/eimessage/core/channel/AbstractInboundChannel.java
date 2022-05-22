package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;

public abstract class AbstractInboundChannel extends AbstractInitializable implements InboundChannel {
    private String name;

    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;

    public AbstractInboundChannel(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public Message<?> poll() {
        return poll(-1);
    }

    @Override
    public Message<?> poll(long timeout) {
        boolean doPoll = pipeline.beforeInbound(this);
        if (!doPoll) {
            return null;
        }
        Message<?> m = this.pollInternal(timeout);
        if (m != null) {
            m = pipeline.afterInbound(this, m);
        }
        return m;
    }

    protected abstract Message<?> pollInternal(long timeout);

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void startup() {
        init();
    }

    @Override
    public void shutdown() {

    }
}
