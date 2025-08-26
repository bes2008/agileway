package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractLifecycle;

public abstract class AbstractMessagePubSubEndpoint<T> extends AbstractLifecycle implements PubSubEndpoint<T> {
    @Nullable
    private MessageMapper<T> messageMapper;

    public final void stop(Runnable callback) {
        shutdown();
        callback.run();
    }


    @Override
    public void setMessageMapper(MessageMapper<T> messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public MessageMapper<T> getMessageMapper() {
        return this.messageMapper;
    }


}
