package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.event.TopicEventListener;
import com.jn.langx.util.function.Function;

/**
 * producer 使用它来根据 Message 计算出要分配的topic
 * @param <M>
 */
public interface TopicAllocator<M> extends Function<M, String>, TopicEventListener {

}
