package com.jn.agileway.dmmq.core.translator;

import com.jn.agileway.dmmq.core.MessageHolder;
import com.jn.agileway.dmmq.core.MessageTranslator;

public class DefaultMessageTranslator<M> implements MessageTranslator<M> {
    private volatile M message;
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
        this.message = null;
    }

    @Override
    public boolean isIdle() {
        return this.message == null;
    }
}
