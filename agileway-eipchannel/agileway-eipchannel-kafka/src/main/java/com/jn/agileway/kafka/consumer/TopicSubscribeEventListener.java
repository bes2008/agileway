package com.jn.agileway.kafka.consumer;

import com.jn.langx.event.EventListener;

public interface TopicSubscribeEventListener extends EventListener<TopicSubscribeEvent> {
    @Override
    public void on(TopicSubscribeEvent event);
}
