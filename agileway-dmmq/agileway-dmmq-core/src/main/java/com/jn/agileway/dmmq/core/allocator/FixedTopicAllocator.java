package com.jn.agileway.dmmq.core.allocator;

import com.jn.agileway.dmmq.core.MessageTopic;
import com.jn.agileway.dmmq.core.TopicAllocator;
import com.jn.agileway.dmmq.core.event.TopicEvent;
import com.jn.agileway.dmmq.core.event.TopicEventType;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;

public class FixedTopicAllocator<M> implements TopicAllocator<M> {

    private final String topic;
    private boolean topicValid = true;

    public FixedTopicAllocator(@NonNull String topic) {
        Preconditions.checkNotNull(topic);
        this.topic = topic;
    }

    @Override
    public String apply(M input) {
        if (topicValid) {
            return topic;
        }
        return null;
    }

    @Override
    public void on(TopicEvent event) {
        if (event.getType() == TopicEventType.REMOVE) {
            MessageTopic topic = event.getSource();
            if (topic.getName().equals(this.topic)) {
                topicValid = false;
            }
            return;
        }

        if (event.getType() == TopicEventType.ADD) {
            MessageTopic topic = event.getSource();
            if (topic.getName().equals(this.topic)) {
                topicValid = true;
            }
            return;
        }
    }
}
