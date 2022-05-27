package com.jn.agileway.kafka.consumer;

public enum TopicSubscribeSourceType {
    /**
     * 代表了 TopicSubscribeEvent 中的 source 是一个 consumer name
     */
    CONSUMER,
    /**
     * 代表了 TopicSubscribeEvent 中的 source 是一个 topic group
     */
    TOPIC_GROUP,
    /**
     * 代表了 TopicSubscribeEvent 中的 source 是一个 自定义的一个标识
     */
    CUSTOM;
}
