package com.jn.agileway.eimessage.core.endpoints.consumer;

import com.jn.agileway.eimessage.core.Message;

/**
 * Base interface for any component that handles Messages.
 *
 */
public interface MessageHandler {

    /**
     * Handles the message if possible. If the handler cannot deal with the
     * message this will result in a <code>MessageRejectedException</code> e.g.
     * in case of a Selective Consumer. When a consumer tries to handle a
     * message, but fails to do so, a <code>MessageHandlingException</code> is
     * thrown. In the last case it is recommended to treat the message as tainted
     * and go into an error scenario.
     * <p/>
     * When the handling results in a failure of another message being sent
     * (e.g. a "reply" message), that failure  will trigger a
     * <code>MessageDeliveryException</code>.
     *
     * @param message the message to be handled
     * reply related to the handling of the message
     */
    void handleMessage(Message<?> message) ;

}
