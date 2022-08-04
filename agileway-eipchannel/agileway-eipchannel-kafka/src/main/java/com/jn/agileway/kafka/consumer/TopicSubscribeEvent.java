package com.jn.agileway.kafka.consumer;

import com.jn.langx.event.DomainEvent;

import java.util.List;

/**
 * source 为 topic group name
 */
public class TopicSubscribeEvent extends DomainEvent<String> {
    private TopicSubscribeEventType type;

    /**
     * 每一个元素的格式： {clusterAddress}/{topic}
     */
    private List<String> topics;

    public TopicSubscribeEvent() {
        super();
    }

    public TopicSubscribeEvent(String eventDomain, TopicSubscribeEventType type, String source) {
        super(eventDomain, source);
    }

    public void setType(TopicSubscribeEventType type) {
        this.type = type;
    }

    public TopicSubscribeEventType getType() {
        return type;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
