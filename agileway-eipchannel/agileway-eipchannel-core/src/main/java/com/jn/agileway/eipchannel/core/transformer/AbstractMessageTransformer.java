package com.jn.agileway.eipchannel.core.transformer;


import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.message.MessageBuilder;

public abstract class AbstractMessageTransformer implements MessageTransformer {

    public final Message<?> transform(Message<?> message) {
        try {
            Object result = this.doTransform(message);
            if (result == null) {
                return null;
            }
            if (result instanceof Message) {
                return (Message) result;
            }
            Message<?> m = MessageBuilder.withPayload(result).copyHeaders(message.getHeaders()).build();
            return m;
        } catch (MessageTransformationException e) {
            throw e;
        } catch (Exception e) {
            throw new MessageTransformationException(message, "failed to transform message： " + e.getMessage(), e);
        }
    }

    /**
     * Subclasses must implement this method to provide the transformation
     * logic. If the return value is itself a Message, it will be used as the
     * result. Otherwise, any non-null return value will be used as the payload
     * of the result Message.
     */
    protected abstract Object doTransform(Message<?> message) throws Exception;

}

