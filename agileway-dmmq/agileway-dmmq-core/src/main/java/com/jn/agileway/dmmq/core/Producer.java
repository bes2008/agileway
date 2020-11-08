package com.jn.agileway.dmmq.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public interface Producer<M> extends TopicAllocatorAware<M>, MessageTopicDispatcherAware {
    void publish(@NonNull M message);

    void publish(@Nullable String topicName, @NonNull M message);
}
