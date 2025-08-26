package com.jn.agileway.distributed.cluster.election;

import com.jn.langx.lifecycle.Lifecycle;

/**
 * 通常要在分布式锁的基础上加上如下功能，即可实现基于分布式锁的leader选举功能：
 * <p>
 *   <pre>
 *   流程如下：
 *      一个节点，还不是 Leader时，就一直尝试加锁。加的锁要有过期时间。
 *      当加锁成功时，节点变成 leader，通知监听器，当前节点已获得锁，并成为leader
 *      当锁被释放时，变成 Flower，仍然一直尝试加锁
 *   </pre>
 *
 * </p>
 * 1. 监听 leader变化:
 * 1.1 加锁成功后，通知监听器，当前节点已获得锁，并成为leader
 * 1.2 成为 leader 后，监听锁的释放，并通知监听器，当前节点已释放锁，并已不是leader
 */
public interface LeaderElector extends Lifecycle {
}
