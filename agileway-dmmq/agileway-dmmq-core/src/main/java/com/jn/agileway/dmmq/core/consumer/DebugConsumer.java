package com.jn.agileway.dmmq.core.consumer;

import com.jn.agileway.dmmq.core.Consumer;
import com.jn.agileway.dmmq.core.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugConsumer<M> implements Consumer<M> {
    private static final Logger logger = LoggerFactory.getLogger(DebugConsumer.class);

    @Override
    public String getName() {
        return "mq debugger";
    }

    @Override
    public void handleMessage(MessageHolder<M> messageHolder, long sequence, boolean endOfBatch) throws Exception {
        logger.info("handling sequence:{}, endOfBatch:{}, message:{}", sequence, endOfBatch, messageHolder.get());
    }

    @Override
    public void handleMessageException(Throwable ex, long sequence, MessageHolder<M> messageHolder) {
        logger.warn("error when handle sequence: {}, message: {}, error:{} ", sequence, messageHolder.get().toString(), ex);
    }

    @Override
    public void handleStartException(Throwable ex) {
        logger.error("error on start the DEBUG consumer, error message: {}, stack:{}", ex.getMessage(), ex);
    }

    @Override
    public void handleShutdownException(Throwable ex) {
        logger.error("error on shutdown the DEBUG consumer, error message: {}, stack:{}", ex.getMessage(), ex);
    }

    @Override
    public void handleTimeout(long sequence) throws Exception {
        logger.warn("Timeout when handle sequence {} message ", sequence);
    }
}
