package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.concurrent.async.DefaultFuture;
import com.jn.langx.util.concurrent.async.GenericFuture;
import com.jn.langx.util.concurrent.executor.ImmediateExecutor;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultInboundChannel extends AbstractLifecycle implements InboundChannel {
    private boolean asyncMode = false;

    private ExecutorService asyncExecutor;

    @NonNull
    private InboundChannelMessageSource inboundMessageSource;

    public void setAsyncExecutor(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public final Message<?> poll() {
        return poll(-1);
    }

    @Override
    public final Message<?> poll(long timeout) {
        return this.pollInternal(timeout);
    }

    /**
     * 方法是同步的，poll动作是根据asyncMode 来控制是否异步执行。
     * @param timeout
     * @return
     */
    protected Message<?> pollInternal(final long timeout) {
        if (!isAsyncMode()) {
            return inboundMessageSource.poll(timeout);
        } else {
            GenericFuture<Message<?>> future = DefaultFuture.submit(this.asyncExecutor, new Callable<Message<?>>() {
                @Override
                public Message<?> call() {
                    return inboundMessageSource.poll(timeout);
                }
            }, false);
            future.awaitUninterruptibly(timeout);
            if (future.isSuccess()) {
                future.getNow();
            }
            return null;
        }
    }

    @Override
    public void doInit() {
        Preconditions.checkNotEmpty(getName(), "the inbound channel's name is required");
        Preconditions.checkNotNull(inboundMessageSource);
        if (asyncMode) {
            Preconditions.checkNotNull(this.asyncExecutor);
        }
    }

    @Override
    protected void doStart() {
        inboundMessageSource.startup();
    }

    @Override
    protected void doStop() {
        inboundMessageSource.shutdown();
    }

    public InboundChannelMessageSource getInboundMessageSource() {
        return inboundMessageSource;
    }

    public void setInboundMessageSource(InboundChannelMessageSource inboundMessageSource) {
        this.inboundMessageSource = inboundMessageSource;
    }

    @Override
    public boolean isAsyncMode() {
        return asyncMode;
    }

    public void setAsyncMode(boolean asyncMode) {
        if (!isRunning()) {
            this.asyncMode = asyncMode;
        }
    }
}
