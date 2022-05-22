package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.pipeline.simplex.SimplexPipeline;

public class PipelineOutboundChannel extends AbstractInitializable implements OutboundChannel {
    @NonNull
    private OutboundChannel outboundChannel;
    @Nullable
    private SimplexPipeline pipeline;

    public PipelineOutboundChannel(OutboundChannel outboundChannel, SimplexPipeline pipeline) {
        this.outboundChannel = outboundChannel;
        this.pipeline = pipeline;
    }

    @Override
    public boolean send(Message<?> message) {
        message = doFilter(message);
        if (message != null) {
            return outboundChannel.send(message);
        }
        return false;
    }

    @Override
    public boolean send(Message<?> message, long timeout) {
        message = doFilter(message);
        if (message != null) {
            return outboundChannel.send(message, timeout);
        }
        return false;
    }

    private Message<?> doFilter(Message<?> message) {
        if (pipeline != null) {
            return (Message) pipeline.handle(message);
        }
        return message;
    }

    @Override
    public Class getDatatype() {
        return outboundChannel.getDatatype();
    }

    @Override
    public void setDataType(Class datatype) {
        outboundChannel.setDataType(datatype);
    }

    @Override
    public void setName(String s) {
        outboundChannel.setName(s);
    }

    @Override
    public String getName() {
        return outboundChannel.getName();
    }


    @Override
    public void startup() {
        outboundChannel.startup();
    }

    @Override
    public void shutdown() {
        outboundChannel.shutdown();
    }
}
