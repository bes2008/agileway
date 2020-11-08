package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.Consumer;
import com.jn.agileway.dmmq.core.MessageHolder;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.TimeoutHandler;

public abstract class DisruptorConsumer<M> implements Consumer<M>, EventHandler<MessageHolder<M>>, ExceptionHandler, TimeoutHandler {
    @Override
    public void onEvent(MessageHolder<M> event, long sequence, boolean endOfBatch) throws Exception {
        handleMessage(event, sequence, endOfBatch);
    }

    @Override
    public void onTimeout(long sequence) throws Exception {
        handleTimeout(sequence);
    }

    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        handleMessageException(ex, sequence, (MessageHolder<M>) event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        handleStartException(ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        handleShutdownException(ex);
    }
}
