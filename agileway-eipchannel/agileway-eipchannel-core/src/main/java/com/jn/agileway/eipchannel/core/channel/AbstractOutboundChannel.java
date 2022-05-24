package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public abstract class AbstractOutboundChannel extends AbstractInitializable implements OutboundChannel {
    protected Logger logger = Loggers.getLogger(getClass());
    private String name;
    private Class payloadClass;
    private OutboundChannelSinker sinker;

    @Override
    public boolean send(Message<?> message) {
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


    public OutboundChannelSinker getSinker() {
        return sinker;
    }

    public void setSinker(OutboundChannelSinker sinker) {
        this.sinker = sinker;
    }


}
