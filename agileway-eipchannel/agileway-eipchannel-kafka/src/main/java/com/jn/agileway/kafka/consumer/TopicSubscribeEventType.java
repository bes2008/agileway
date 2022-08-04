package com.jn.agileway.kafka.consumer;

public enum TopicSubscribeEventType {
    /**
     * 新订阅 topics
     */
    ADD,
    /**
     * 取消指定的topics的订阅
     */
    DELETE,
    /**
     * 重新订阅
     */
    REPLACE
}
