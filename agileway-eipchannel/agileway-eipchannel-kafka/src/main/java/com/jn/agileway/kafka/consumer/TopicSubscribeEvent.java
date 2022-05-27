package com.jn.agileway.kafka.consumer;

import com.jn.langx.event.DomainEvent;

import java.util.List;

/**
 * source 为 consumer 相关的 标示符。例如 consumer name 获取其他的能够唯一标识 一个 consumer 的数据
 */
public class TopicSubscribeEvent extends DomainEvent<String> {
    private TopicSubscribeEventType type;
    private List<String> topics;
    private TopicSubscribeSourceType sourceType;
    public TopicSubscribeEvent(){
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
