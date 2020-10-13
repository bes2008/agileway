package com.jn.agileway.dmmq.core.event;

import com.jn.agileway.dmmq.core.MessageTopic;
import com.jn.langx.event.DomainEvent;

public class TopicEvent extends DomainEvent<MessageTopic> {
    public static final String DOMAIN = "TOPIC";
    private TopicEventType type;

    public TopicEvent(MessageTopic source) {
        super(DOMAIN, source);
    }

    public TopicEvent(MessageTopic source, TopicEventType type) {
        super(DOMAIN, source);
        setType(type);
    }


    public TopicEventType getType() {
        return type;
    }

    public void setType(TopicEventType type) {
        this.type = type;
    }
}
