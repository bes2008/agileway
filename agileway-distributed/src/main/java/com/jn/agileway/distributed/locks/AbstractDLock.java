package com.jn.agileway.distributed.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public abstract class AbstractDLock implements DLock {
    @Override
    public void lock() {
        tryLock(-1, TimeUnit.MILLISECONDS, -1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock(-1, TimeUnit.MILLISECONDS, true);
    }

    @Override
    public void lock(long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException {
        tryLock(-1, TimeUnit.MILLISECONDS, ttl, ttlUnit);
    }

    @Override
    public boolean tryLock(long tryTime, TimeUnit tryTimeUnit, long ttl, TimeUnit ttlUnit) {
        try {
            return tryLock(tryTime, tryTimeUnit, ttl, ttlUnit, false);
        } catch (InterruptedException ex) {
            boolean interrupted=Thread.interrupted();
            if(interrupted){
                // ignore it
            }
            return false;
        }
    }

    @Override
    public boolean tryLock(long tryTime, TimeUnit tryUnit, long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException {
        return false;
    }

    @Override
    public boolean tryLock(long tryTime, TimeUnit tryTimeUnit) {
        return tryLock(tryTime, tryTimeUnit, -1, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock() {
        return tryLock(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unlock() {
        forceUnlock();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("newCondition is not unsupported for distributed lock");
    }

}
