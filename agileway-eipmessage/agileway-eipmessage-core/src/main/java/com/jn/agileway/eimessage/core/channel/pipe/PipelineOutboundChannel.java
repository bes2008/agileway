package com.jn.agileway.eimessage.core.channel.pipe;

import com.jn.agileway.eimessage.core.channel.OutboundChannel;
import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;

public class PipelineOutboundChannel extends AbstractInitializable implements OutboundChannel {
    @NonNull
    private OutboundChannel outboundChannel;
    @Nullable
    private ChannelMessageInterceptorPipeline pipeline;

    public PipelineOutboundChannel(OutboundChannel outboundChannel, ChannelMessageInterceptorPipeline pipeline) {
        this.outboundChannel = outboundChannel;
        this.pipeline = pipeline;
    }

    @Override
    public boolean send(Message<?> message) {
        return send(message, -1);
    }

    @Override
    public boolean send(Message<?> message, long timeout) {
        message = pipeline.beforeOutbound(outboundChannel, message);
        if (message == null) {
            return false;
        }
        boolean sent = outboundChannel.send(message, timeout);
        pipeline.afterOutbound(outboundChannel, message, sent);
        return sent;
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
