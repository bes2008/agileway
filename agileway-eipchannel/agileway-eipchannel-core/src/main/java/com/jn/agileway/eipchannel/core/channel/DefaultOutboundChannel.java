package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public class DefaultOutboundChannel extends AbstractInitializable implements OutboundChannel {
    protected Logger logger = Loggers.getLogger(getClass());
    @NotEmpty
    private String name;
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
    public void startup() {
        init();
        Preconditions.checkNotEmpty(getName(),"the outbound channel's name is required");
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public OutboundChannelSinker getSinker() {
        return sinker;
    }

    public void setSinker(OutboundChannelSinker sinker) {
        this.sinker = sinker;
    }


}
