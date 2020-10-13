package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.event.TopicEventListener;
import com.jn.langx.util.function.Function;

public interface TopicAllocator<M> extends Function<M, String>, TopicEventListener {

}
