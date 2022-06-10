package com.jn.agileway.dmmq.core;


public class DefaultMessageTranslator<M> implements MessageTranslator<M> {
    @Override
    public void translateTo(MessageHolder<M> event, long sequence, String topic, M message) {
        event.setTopicName(topic);
        event.set(message);
    }
}
