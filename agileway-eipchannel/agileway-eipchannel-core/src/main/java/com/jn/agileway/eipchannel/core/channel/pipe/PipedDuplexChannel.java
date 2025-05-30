package com.jn.agileway.eipchannel.core.channel.pipe;

import com.jn.agileway.eipchannel.core.channel.DuplexChannel;
import com.jn.agileway.eipchannel.core.channel.PipedInboundChannel;
import com.jn.agileway.eipchannel.core.channel.PipedOutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;

import java.net.http.HttpRequest;

public class PipedDuplexChannel extends AbstractLifecycle implements DuplexChannel {
    private PipedInboundChannel inboundChannel;

    private PipedOutboundChannel outboundChannel;


    public PipedDuplexChannel(PipedOutboundChannel outboundChannel, PipedInboundChannel inboundChannel) {
        this.inboundChannel = inboundChannel;
        this.outboundChannel = outboundChannel;
    }

    @Override
    public Message<?> poll() {
        return inboundChannel.poll();
    }

    @Override
    public Message<?> poll(long timeout) {
        return inboundChannel.poll(timeout);
    }

    @Override
    public boolean send(Message<?> message) {
        return outboundChannel.send(message);
    }

    @Override
    public Class getDatatype() {
        return outboundChannel.getDatatype();
    }

    @Override
    public void setDataType(Class datatype) {
        outboundChannel.setDataType(datatype);
    }
}
