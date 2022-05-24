package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractMessagePubSubEndpoint<T> extends AbstractInitializable implements PubSubEndpoint<T> {
    @Nullable
    private MessageMapper<T> messageMapper;
    @NotEmpty
    private String name;

    private volatile boolean autoStartup = true;

    private volatile int phase = 0;

    private volatile boolean running;

    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private Logger logger = Loggers.getLogger(getClass());


    public final boolean isRunning() {
        this.lifecycleLock.lock();
        try {
            return this.running;
        } finally {
            this.lifecycleLock.unlock();
        }
    }

    public final void startup() {
        this.lifecycleLock.lock();
        try {
            if (!this.running) {
                this.init();
                this.doStart();
                this.running = true;
                if (logger.isInfoEnabled()) {
                    logger.info("started " + this);
                }
            }
        } finally {
            this.lifecycleLock.unlock();
        }
    }

    public final void shutdown() {
        this.lifecycleLock.lock();
        try {
            if (this.running) {
                this.doStop();
                this.running = false;
                if (logger.isInfoEnabled()) {
                    logger.info("stopped " + this);
                }
            }
        } finally {
            this.lifecycleLock.unlock();
        }
    }

    public final void stop(Runnable callback) {
        this.lifecycleLock.lock();
        try {
            this.shutdown();
            callback.run();
        } finally {
            this.lifecycleLock.unlock();
        }
    }

    /**
     * Subclasses must implement this method with the start behavior.
     * This method will be invoked while holding the {@link #lifecycleLock}.
     */
    protected abstract void doStart();

    /**
     * Subclasses must implement this method with the stop behavior.
     * This method will be invoked while holding the {@link #lifecycleLock}.
     */
    protected abstract void doStop();

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }


    // SmartLifecycle implementation

    public final boolean isAutoStartup() {
        return this.autoStartup;
    }

    public final int getPhase() {
        return this.phase;
    }

    @Override
    public void setMessageMapper(MessageMapper<T> messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public MessageMapper<T> getMessageMapper() {
        return this.messageMapper;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

}
