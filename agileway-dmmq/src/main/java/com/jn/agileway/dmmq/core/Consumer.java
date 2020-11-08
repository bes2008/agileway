package com.jn.agileway.dmmq.core;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.TimeoutHandler;

public interface Consumer<M> extends EventHandler<MessageHolder<M>>, ExceptionHandler, TimeoutHandler {
    String getName();
}
