package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;

public class DefaultOutboundChannel extends AbstractLifecycle implements OutboundChannel {
    @Nullable
    private Class payloadClass;
    @NonNull
    private OutboundChannelSinker sinker;

    @Override
    public final boolean send(Message<?> message) {
        return sendInternal(message);
    }

    protected boolean sendInternal(Message<?> message) {
        return this.sinker.sink(message);
    }


    @Override
    public Class getDatatype() {
        return this.payloadClass;
    }

    @Override
    public void setDataType(Class datatype) {
        this.payloadClass = datatype;
    }

    @Override
    protected void doInit() throws InitializationException {
        Preconditions.checkNotEmpty(getName(),"the outbound channel's name is required");
        Preconditions.checkNotNull(sinker);
    }

    @Override
    protected void doStart() {
        sinker.startup();
    }

    @Override
    protected void doStop() {
        sinker.shutdown();
    }

    public OutboundChannelSinker getSinker() {
        return sinker;
    }

    public void setSinker(OutboundChannelSinker sinker) {
        this.sinker = sinker;
    }


}
