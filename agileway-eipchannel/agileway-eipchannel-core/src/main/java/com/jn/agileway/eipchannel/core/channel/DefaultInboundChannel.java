package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.Preconditions;

public class DefaultInboundChannel extends AbstractLifecycle implements InboundChannel {


    @NonNull
    private InboundChannelMessageSource inboundMessageSource;


    @Override
    public final Message<?> poll() {
        return poll(-1);
    }

    @Override
    public final Message<?> poll(final long timeout) {
            return pollInternal(timeout);
    }

    /**
     * 方法是同步的，poll动作是根据asyncMode 来控制是否异步执行。
     *
     */
    protected Message<?> pollInternal(final long timeout) {
        return inboundMessageSource.poll(timeout);
    }

    @Override
    public void doInit() {
        Preconditions.checkNotEmpty(getName(), "the inbound channel's name is required");
        Preconditions.checkNotNull(inboundMessageSource);
    }

    @Override
    protected void doStart() {
        inboundMessageSource.startup();
    }

    @Override
    protected void doStop() {
        inboundMessageSource.shutdown();
    }

    public InboundChannelMessageSource getInboundMessageSource() {
        return inboundMessageSource;
    }

    public void setInboundMessageSource(InboundChannelMessageSource inboundMessageSource) {
        this.inboundMessageSource = inboundMessageSource;
    }

}
