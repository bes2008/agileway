package com.jn.agileway.eimessage.core.transformer;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.agileway.eimessage.core.message.MessagingException;

public class MessageTransformationException extends MessagingException {

    public MessageTransformationException(Message<?> message, String description, Throwable cause) {
        super(message, description, cause);
    }

    public MessageTransformationException(Message<?> message, String description) {
        super(message, description);
    }

    public MessageTransformationException(Message<?> message, Throwable cause) {
        super(message, cause);
    }

    public MessageTransformationException(String description, Throwable cause) {
        super(description, cause);
    }

    public MessageTransformationException(String description) {
        super(description);
    }

}

