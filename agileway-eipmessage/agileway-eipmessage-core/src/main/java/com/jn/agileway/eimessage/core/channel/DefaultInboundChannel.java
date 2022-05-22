package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DefaultInboundChannel extends AbstractInboundChannel {
    private BlockingQueue<Message<?>> queue;

    @Override
    protected Message<?> pollInternal(long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public BlockingQueue getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue queue) {
        this.queue = queue;
    }
}
