package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public abstract class AbstractOutboundChannel extends AbstractInitializable implements OutboundChannel {
    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;
    private String name;
    private Class payloadClass;
    protected Logger logger = Loggers.getLogger(getClass());

    @Override
    public boolean send(Message<?> message) {
        message = pipeline.beforeOutbound(this, message);
        if (message == null) {
            return false;
        }
        boolean sent = this.sendInternal(message);
        pipeline.afterOutbound(this, message, sent);
        return sent;
    }

    protected abstract boolean sendInternal(Message<?> message);

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
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setName(String s) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public ChannelMessageInterceptorPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ChannelMessageInterceptorPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
