package com.jn.agileway.distributed.cluster.election;

import com.jn.langx.lifecycle.Lifecycle;

/**
 * 通常要在分布式锁的基础上加上如下功能，即可实现基于分布式锁的leader选举功能：
 * <p>
 * 1. 监听 leader变化:
 * 1.1 加锁成功后，通知监听器，当前节点已获得锁，并成为leader
 * 1.2 成为 leader 后，监听锁的释放，并通知监听器，当前节点已释放锁，并已不是leader
 */
public interface LeaderElector extends Lifecycle {
}
