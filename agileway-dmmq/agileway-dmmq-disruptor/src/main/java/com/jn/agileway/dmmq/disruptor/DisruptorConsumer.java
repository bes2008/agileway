package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.Consumer;
import com.jn.agileway.dmmq.core.MessageHolder;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.TimeoutHandler;

public interface DisruptorConsumer<M> extends Consumer<M>, EventHandler<MessageHolder<M>>, ExceptionHandler, TimeoutHandler {
}
