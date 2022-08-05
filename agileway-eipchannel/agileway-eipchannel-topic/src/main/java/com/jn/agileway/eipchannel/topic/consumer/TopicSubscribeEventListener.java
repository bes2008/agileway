package com.jn.agileway.eipchannel.topic.consumer;

import com.jn.langx.event.EventListener;

public interface TopicSubscribeEventListener extends EventListener<TopicSubscribeEvent> {
    @Override
    public void on(TopicSubscribeEvent event);
}
