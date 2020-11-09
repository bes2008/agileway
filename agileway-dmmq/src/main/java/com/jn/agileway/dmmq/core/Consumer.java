package com.jn.agileway.dmmq.core;

import com.jn.langx.util.struct.Holder;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.TimeoutHandler;

public interface Consumer<M> extends EventHandler<MessageHolder<M>>, ExceptionHandler<Holder<M>>, TimeoutHandler {
    String getName();
}
