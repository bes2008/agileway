package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.allocator.DefaultTopicAllocator;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.reflect.Reflects;

import java.util.concurrent.Executor;

public class MessageTopicConfiguration {
    @NonNull
    private String name = DefaultTopicAllocator.TOPIC_DEFAULT;

    @NonNull
    private Executor executor;



    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
