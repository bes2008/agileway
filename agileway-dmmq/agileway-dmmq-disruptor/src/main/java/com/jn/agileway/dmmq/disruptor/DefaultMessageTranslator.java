package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.MessageHolder;

public class DefaultMessageTranslator<M> implements DisruptorMessageTranslator<M> {
    private M message;
    private String topicName;

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void setMessage(M message) {
        this.message = message;
    }

    @Override
    public M getMessage() {
        return message;
    }

    @Override
    public void translateTo(MessageHolder<M> event, long sequence) {
        event.setTopicName(getTopicName());
        event.set(message);
    }
}
