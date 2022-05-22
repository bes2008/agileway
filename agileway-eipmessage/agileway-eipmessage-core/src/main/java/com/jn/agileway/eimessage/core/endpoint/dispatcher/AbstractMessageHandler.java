package com.jn.agileway.eimessage.core.endpoint.dispatcher;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public abstract class AbstractMessageHandler extends AbstractInitializable implements MessageHandler {
    protected Logger logger = Loggers.getLogger(getClass());
    @Override
    public  final void handle(Message<?> message) {
        this.handleMessageInternal(message);
    }

    protected abstract void handleMessageInternal(Message<?> message);
}
