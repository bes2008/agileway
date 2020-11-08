package com.jn.agileway.dmmq.core;

import com.jn.langx.util.struct.Holder;

public class MessageHolder<M> extends Holder<M> implements TopicNameAware {
    private String topicName;

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
