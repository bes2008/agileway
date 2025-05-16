package com.jn.agileway.distributed.locks;

import com.jn.langx.util.Dates;
import com.jn.langx.util.logging.Loggers;

import java.util.concurrent.TimeUnit;

public abstract class DistributedLock extends AbstractDLock {


    @Override
    public boolean tryLock(long tryTime, TimeUnit tryUnit, long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException {
        Object v = getValue();
        tryUnit = tryUnit == null ? TimeUnit.MILLISECONDS : tryUnit;
        ttlUnit = ttlUnit == null ? TimeUnit.MILLISECONDS : ttlUnit;

        boolean locked = false;

        long start = System.currentTimeMillis();
        // 加锁还需要等待的时间
        long waitTime = tryTime > 0 ? tryUnit.toMillis(tryTime) : 5L;
        long endTime = start + waitTime;
        while (!locked && waitTime > 0) {
            locked = doLock(v, ttl, ttlUnit);
            waitTime = endTime - System.currentTimeMillis();
            if (!locked && waitTime > 0) {
                try {
                    synchronized (this) {
                        this.wait(50);
                    }
                } catch (InterruptedException ex) {
                    if (interruptibly) {
                        throw ex;
                    }
                }
            }
            long now = System.currentTimeMillis();
            waitTime = endTime - now;
            if(now - start> Dates.MINUTES_TO_MILLIS){
                Loggers.getLogger(getClass()).warn("LOCK SLOW :: key: {}, startTime: {}, pendingTime:{}", this.getKey(), start, now - start);
            }
        }
        if (locked) {
            setValue(v);
        }
        return locked;
    }


    protected abstract String getKey();
    protected abstract Object getValue();
    protected abstract void setValue(Object value);
    protected abstract boolean doLock(Object value, long ttl, TimeUnit ttlUnit);

}
