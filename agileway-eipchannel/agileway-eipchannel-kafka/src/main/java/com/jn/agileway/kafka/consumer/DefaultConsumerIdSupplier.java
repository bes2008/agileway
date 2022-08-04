package com.jn.agileway.kafka.consumer;

import com.jn.langx.text.StringTemplates;

public class DefaultConsumerIdSupplier implements ConsumerIdSupplier {
    @Override
    public String get(TopicSubscribeEvent e) {
        TopicSubscribeSourceType subscribeSourceType = e.getSourceType();
        if (subscribeSourceType == TopicSubscribeSourceType.TOPIC_GROUP) {
            StringTemplates.formatWithPlaceholder("{}/{}", e.getSource(), e.getClusterAddress());
        } else if (subscribeSourceType == TopicSubscribeSourceType.CONSUMER) {
            return e.getSource();
        }
        return null;
    }
}
