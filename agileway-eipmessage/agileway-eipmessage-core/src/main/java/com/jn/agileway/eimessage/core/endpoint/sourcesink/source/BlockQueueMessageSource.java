package com.jn.agileway.eimessage.core.endpoint.sourcesink.source;


import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockQueueMessageSource implements InboundMessageSource {
    private BlockingQueue<Message<?>> queue;
    private static final Logger logger = Loggers.getLogger(BlockQueueMessageSource.class);

    @Override
    public Message<?> poll(long timeoutInMills) {
        try {
            if (timeoutInMills > 0) {
                return queue.poll(timeoutInMills, TimeUnit.MILLISECONDS);
            }
            if (timeoutInMills == 0) {
                return queue.poll();
            }
            return queue.take();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public BlockingQueue<Message<?>> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Message<?>> queue) {
        this.queue = queue;
    }
}
