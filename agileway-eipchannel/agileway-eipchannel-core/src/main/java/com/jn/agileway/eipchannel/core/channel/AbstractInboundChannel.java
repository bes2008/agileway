package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public abstract class AbstractInboundChannel extends AbstractInitializable implements InboundChannel {
    private String name;
    protected Logger logger = Loggers.getLogger(getClass());
    private InboundChannelMessageSource inboundMessageSource;



    @Override
    public Message<?> poll() {
        return poll(-1);
    }

    @Override
    public Message<?> poll(long timeout) {
        return this.pollInternal(timeout);
    }

    protected Message<?> pollInternal(long timeout) {
        return inboundMessageSource.poll(timeout);
    }

    @Override
    public void startup() {
        init();
    }

    @Override
    public void shutdown() {

    }

    public InboundChannelMessageSource getInboundMessageSource() {
        return inboundMessageSource;
    }

    public void setInboundMessageSource(InboundChannelMessageSource inboundMessageSource) {
        this.inboundMessageSource = inboundMessageSource;
    }
    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
    }


}
