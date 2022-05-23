package com.jn.agileway.eipchannel.core.channel;

import com.jn.langx.Nameable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.Lifecycle;

/**
 * Base channel interface defining common behavior for sending messages.
 * 用于代表内存级channel，或者分布式 channel
 * <p>
 * 使用它可以用来接收消息，也可以用来发布消息。
 * <p>
 * 用它来接收时，源头可以是一个，也可以是多个
 */
public interface MessageChannel extends Nameable, Lifecycle, Initializable {

}
