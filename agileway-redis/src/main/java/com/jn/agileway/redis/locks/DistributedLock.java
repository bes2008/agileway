package com.jn.agileway.redis.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * https://redis.io/topics/distlock
 */
public abstract class DistributedLock implements Lock {
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("newCondition is not unsupported for distributed lock");
    }
}
