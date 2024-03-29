package com.jn.agileway.eipchannel.core.endpoint.sourcesink.source;


import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueInboundChannelMessageSource extends AbstractLifecycle implements InboundChannelMessageSource {
    private BlockingQueue<Message<?>> queue;
    private static final Logger logger = Loggers.getLogger(BlockingQueueInboundChannelMessageSource.class);

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

    public void setQueue(BlockingQueue<Message<?>> queue) {
        this.queue = queue;
    }
}
