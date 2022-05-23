package com.jn.agileway.eipchannel.eventchannel.sink;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.DispatcherOutboundChannelSinker;
import com.jn.agileway.eipchannel.core.router.MessageRouter;

public class EventOutboundRemoteSinker extends DispatcherOutboundChannelSinker {
    public void addMessageRouter(MessageRouter router) {
        getDispatcher().addHandler(router);
    }

    public void remoteMessageRouter(MessageRouter router) {
        getDispatcher().removeHandler(router);
    }
}
