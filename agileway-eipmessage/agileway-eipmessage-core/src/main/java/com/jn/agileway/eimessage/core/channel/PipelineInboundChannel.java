package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.pipeline.simplex.SimplexPipeline;

public class PipelineInboundChannel implements InboundChannel {
    @NonNull
    private InboundChannel inboundChannel;
    @Nullable
    private SimplexPipeline pipeline;

    public PipelineInboundChannel(InboundChannel channel, SimplexPipeline pipeline) {
        this.inboundChannel = channel;
        this.pipeline = pipeline;
    }

    @Override
    public Message<?> poll() {
        Message<?> m = inboundChannel.poll();
        m = doFilter(m);
        return m;
    }

    @Override
    public Message<?> poll(long timeout) {
        Message<?> m = inboundChannel.poll(timeout);
        m = doFilter(m);
        return m;
    }

    private Message<?> doFilter(Message<?> message) {
        if (pipeline != null) {
            pipeline.handle(message);
        }
        return message;
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
    public void init() throws InitializationException {
        inboundChannel.init();
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
