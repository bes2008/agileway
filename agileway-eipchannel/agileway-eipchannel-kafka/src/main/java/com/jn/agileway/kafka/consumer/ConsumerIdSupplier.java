package com.jn.agileway.kafka.consumer;

import com.jn.langx.util.function.Supplier;

public interface ConsumerIdSupplier extends Supplier<TopicSubscribeEvent, String> {
    @Override
    String get(TopicSubscribeEvent e);
}
