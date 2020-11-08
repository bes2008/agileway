package com.jn.agileway.dmmq.core;

public interface Consumer<M> {
    String getName();
    void handleMessage(MessageHolder<M> message, long sequence, boolean endOfBatch) throws Exception;

    void handleMessageException(Throwable ex, long sequence, MessageHolder<M> message);

    void handleStartException(Throwable ex);

    void handleShutdownException(Throwable ex);

    void handleTimeout(long sequence) throws Exception;
}
