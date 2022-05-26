package com.jn.agileway.kafka.consumer;

import com.jn.langx.util.function.Supplier;

public interface ConsumerIdSupplier extends Supplier<String, String> {
    @Override
    String get(String s);
}
