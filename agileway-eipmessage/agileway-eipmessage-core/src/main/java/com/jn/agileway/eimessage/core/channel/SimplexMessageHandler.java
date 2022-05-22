package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.pipeline.simplex.SimplexHandler;

public interface SimplexMessageHandler extends SimplexHandler<Message<?>,Message<?>> {
    @Override
    Message<?> apply(Message<?> message);
}
