package com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.router.MessageRouter;

public class OutboundChannelRouterSinkerAdapter implements OutboundChannelSinker {
    private MessageRouter router;

    public OutboundChannelRouterSinkerAdapter() {
    }

    public OutboundChannelRouterSinkerAdapter(MessageRouter router) {
        setRouter(router);
    }

    public void setRouter(MessageRouter router) {
        this.router = router;
    }

    @Override
    public boolean sink(Message<?> message) {
        router.handle(message);
        return true;
    }

    @Override
    public void startup() {
        router.startup();
    }

    @Override
    public void shutdown() {
        router.shutdown();
    }
}
