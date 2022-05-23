package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.endpoint.dispatcher.MessageHandler;
import com.jn.agileway.eipchannel.core.message.Message;

public class PollingConsumer extends AbstractPollingConsumer {
    private MessageHandler messageHandler;

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        setMessageMapper(null);
    }

    @Override
    public Object poll(long timeout) {
        Object message = super.poll(timeout);
        if (message instanceof Message) {
            messageHandler.handle((Message<?>) message);
        }
        return message;
    }
}
