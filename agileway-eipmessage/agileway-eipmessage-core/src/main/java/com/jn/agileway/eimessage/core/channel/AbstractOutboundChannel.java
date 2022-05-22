package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;

public abstract class AbstractOutboundChannel extends AbstractInitializable implements OutboundChannel {
    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;
    private String name;
    private Class payloadClass;

    public AbstractOutboundChannel(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public boolean send(Message<?> message) {
        return send(message, -1);
    }

    @Override
    public boolean send(Message<?> message, long timeout) {
        message = pipeline.beforeOutbound(this, message);
        if (message == null) {
            return false;
        }
        boolean sent = this.sendInternal(message, timeout);
        pipeline.afterOutbound(this, message, sent);
        return sent;
    }

    protected abstract boolean sendInternal(Message<?> message, long timeout);

    @Override
    public Class getDatatype() {
        return this.payloadClass;
    }

    @Override
    public void setDataType(Class datatype) {
        this.payloadClass = datatype;
    }

    @Override
    public void setName(String s) {
        this.name = name;
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
