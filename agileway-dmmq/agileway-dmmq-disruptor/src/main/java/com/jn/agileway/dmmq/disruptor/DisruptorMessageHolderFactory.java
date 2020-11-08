package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.MessageHolder;
import com.lmax.disruptor.EventFactory;

public class DisruptorMessageHolderFactory<M> implements EventFactory<MessageHolder<M>> {
    @Override
    public MessageHolder<M> newInstance() {
        return new MessageHolder<M>();
    }
}
