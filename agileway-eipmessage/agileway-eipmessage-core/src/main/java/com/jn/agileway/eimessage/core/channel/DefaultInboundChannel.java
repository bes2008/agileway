package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;

public class DefaultInboundChannel extends AbstractInboundChannel{

    @Override
    protected Message<?> pollInternal(long timeout) {
        return null;
    }
}
