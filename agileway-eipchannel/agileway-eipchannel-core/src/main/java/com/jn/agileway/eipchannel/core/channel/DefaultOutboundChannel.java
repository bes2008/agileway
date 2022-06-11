package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.concurrent.async.DefaultFuture;
import com.jn.langx.util.concurrent.async.GenericFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class DefaultOutboundChannel extends AbstractLifecycle implements OutboundChannel {
    private boolean asyncMode;
    @Nullable
    private Class payloadClass;
    @NonNull
    private OutboundChannelSinker sinker;

    private ExecutorService asyncExecutor;

    @Override
    public final boolean send(final Message<?> message) {
        if (!isAsyncMode()) {
            return sendInternal(message);
        } else {
            GenericFuture<Boolean> future = DefaultFuture.submit(this.asyncExecutor, new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return sendInternal(message);
                }
            }, false);
            future.awaitUninterruptibly();
            if (future.isSuccess()) {
                future.getNow();
            }
            return false;
        }
    }

    protected boolean sendInternal(final Message<?> message) {
        return this.sinker.sink(message);
    }

    public void setAsyncExecutor(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public Class getDatatype() {
        return this.payloadClass;
    }

    @Override
    public void setDataType(Class datatype) {
        this.payloadClass = datatype;
    }

    @Override
    protected void doInit() throws InitializationException {
        Preconditions.checkNotEmpty(getName(), "the outbound channel's name is required");
        Preconditions.checkNotNull(sinker);
        if (asyncMode) {
            Preconditions.checkNotNull(this.asyncExecutor);
        }

    }

    @Override
    protected void doStart() {
        sinker.startup();
    }

    @Override
    protected void doStop() {
        sinker.shutdown();
    }

    public OutboundChannelSinker getSinker() {
        return sinker;
    }

    public void setSinker(OutboundChannelSinker sinker) {
        this.sinker = sinker;
    }

    @Override
    public boolean isAsyncMode() {
        return false;
    }

    @Override
    public void setAsyncMode(boolean asyncMode) {
        if (!isRunning()) {
            this.asyncMode = asyncMode;
        }
    }
}
