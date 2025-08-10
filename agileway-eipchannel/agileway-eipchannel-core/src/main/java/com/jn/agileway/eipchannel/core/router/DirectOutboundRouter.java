package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;

/**
 * @see Point2PointMessageRouter
 * @deprecated
 */
public class DirectOutboundRouter extends Point2PointMessageRouter {
    public DirectOutboundRouter(OutboundChannel outboundChannel) {
        super(outboundChannel);
    }
}
