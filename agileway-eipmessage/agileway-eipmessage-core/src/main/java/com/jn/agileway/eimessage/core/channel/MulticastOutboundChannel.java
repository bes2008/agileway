package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.handler.router.MessageRouter;

public interface MulticastOutboundChannel extends OutboundChannel{
    MessageRouter getMessageRouter();
}
