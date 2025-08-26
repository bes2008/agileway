package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.endpoint.dispatcher.MessageHandler;
import com.jn.langx.lifecycle.Lifecycle;

/**
 * 消息路由，只能用于 outboundChannel。
 * <p>
 * 使用不同的 MessageRouter 实现，实现不同的 outboundChannel。
 * <p>
 *
 * @see com.jn.agileway.eipchannel.core.router.PubSubMessageRouter
 * @see com.jn.agileway.eipchannel.core.router.Point2PointMessageRouter
 */
public interface MessageRouter extends MessageHandler, Lifecycle {

}
