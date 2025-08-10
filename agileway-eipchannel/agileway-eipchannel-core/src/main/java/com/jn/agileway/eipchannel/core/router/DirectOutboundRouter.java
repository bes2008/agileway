package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;

/**
 * @deprecated
 */
public class DirectOutboundRouter extends Point2PointMessageRouter {
    public DirectOutboundRouter(OutboundChannel defaultOutboundChannel) {
        super(defaultOutboundChannel);
    }
}
