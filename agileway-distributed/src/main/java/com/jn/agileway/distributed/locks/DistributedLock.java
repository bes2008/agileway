package com.jn.agileway.distributed.locks;

import com.jn.langx.util.Dates;
import com.jn.langx.util.Maths;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.retry.WaitStrategy;

import java.util.concurrent.TimeUnit;

public abstract class DistributedLock extends AbstractDLock {

    private WaitStrategy waitStrategy = new WaitStrategy() {
        @Override
        public void await(long waitTimeMills) throws InterruptedException {
            synchronized (this) {
                this.wait(Maths.minLong(50L, waitTimeMills));
            }
        }
    };

    public void setWaitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    @Override
    public boolean tryLock(long tryTime, TimeUnit tryUnit, long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException {
        String v = getValue();
        tryUnit = tryUnit == null ? TimeUnit.MILLISECONDS : tryUnit;
        ttlUnit = ttlUnit == null ? TimeUnit.MILLISECONDS : ttlUnit;

        boolean locked = false;

        long start = System.currentTimeMillis();
        // 加锁还需要等待的时间
        long waitTime = tryTime > 0 ? tryUnit.toMillis(tryTime) : Long.MAX_VALUE;
        long endTime = tryTime <= 0L ? Long.MAX_VALUE : (start + waitTime);
        while (!locked && waitTime > 0) {
            locked = doLock(v, ttl, ttlUnit);
            long now = System.currentTimeMillis();
            waitTime = tryTime > 0 ? (endTime - now) : Long.MAX_VALUE;
            if (!locked && waitTime > 0) {
                try {
                    waitStrategy.await(waitTime);
                } catch (InterruptedException ex) {
                    if (interruptibly) {
                        throw ex;
                    }
                }
            }
            now = System.currentTimeMillis();
            waitTime = tryTime > 0 ? (endTime - now) : Long.MAX_VALUE;
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

    protected abstract String getValue();

    protected abstract void setValue(String value);

    protected abstract boolean doLock(String value, long ttl, TimeUnit ttlUnit);

}
