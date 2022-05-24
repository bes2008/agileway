package com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink;

import com.jn.agileway.eipchannel.core.message.Message;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueOutboundChannelSinker implements OutboundChannelSinker {
    private BlockingQueue<Message<?>> queue;

    public BlockingQueue<Message<?>> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Message<?>> queue) {
        this.queue = queue;
    }

    @Override
    public boolean sink(Message<?> message) {
        return queue.offer(message);
    }
}
