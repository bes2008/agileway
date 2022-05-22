package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.pipeline.simplex.SimplexPipeline;

public class PipelineOutboundChannel implements OutboundChannel {
    private OutboundChannel outboundChannel;
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
        return null;
    }

    @Override
    public void setDataType(Class datatype) {

    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }
}
