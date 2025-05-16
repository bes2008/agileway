package com.jn.agileway.distributed.distributed.locks;

import com.jn.langx.annotation.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface DLock extends Lock {
    /**
     * @deprecated 该方法故意设置为 deprecated, 原因是 该方法 该方法继承自 {@link Lock#lock()},
     * 在没有拿到锁的时候，会死等，直到拿到锁为止。
     * 在分布式场景下要慎用，甚至不要去使用。除非场景必须这样做才去选用。
     * <p>
     * <p>
     * 推荐用 {@link #lock(long, TimeUnit, boolean)}
     */
    @Override
    void lock();
    void lockInterruptibly() throws InterruptedException;

    /**
     * 没拿到锁之前死等，拿到锁之后，在指定的 ttl 时间后，锁自动过期。
     *
     * @param ttl           指定的锁定时间，即拿到锁后过期时间
     * @param ttlUnit       单位
     * @param interruptibly 在尝试加锁期间，能否被打断
     * @throws InterruptedException 锁住后，被打断时，抛出该异常
     */
    void lock(long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException;

    /**
     * 拿到锁之前，最多尝试 lockTime 时间，拿到锁之后，锁在 ttl 后过期
     *
     * @param tryTime     尝试获取锁的时间
     * @param tryTimeUnit 单位
     * @param ttl         拿到锁之后，锁的过期时间
     * @param ttlUnit     单位
     * @return 是否成功上锁
     */
    boolean tryLock(long tryTime, @Nullable TimeUnit tryTimeUnit, long ttl, @Nullable TimeUnit ttlUnit);

    boolean tryLock(long tryTime, @Nullable TimeUnit tryTimeUnit, long ttl, @Nullable TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException;

    @Override
    boolean tryLock(long tryTime, TimeUnit tryTimeUnit);

    /**
     * 只进行一次尝试，锁成功 返回 true, 否则返回false
     */
    @Override
    boolean tryLock();

    void forceUnlock();
}
