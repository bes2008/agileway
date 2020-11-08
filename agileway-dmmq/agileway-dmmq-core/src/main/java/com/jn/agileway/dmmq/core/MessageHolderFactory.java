package com.jn.agileway.dmmq.core;

import com.lmax.disruptor.EventFactory;

public class MessageHolderFactory<M> implements EventFactory<MessageHolder<M>> {
    @Override
    public MessageHolder<M> newInstance() {
        return new MessageHolder<M>();
    }
}
