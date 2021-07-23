package com.jn.agileway.redis.locks;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.Builder;
import com.jn.langx.annotation.NotThreadSafe;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.lock.DistributedLock;

import java.util.concurrent.TimeUnit;

/**
 * https://redis.io/topics/distlock
 */
@NotThreadSafe
public class ExclusiveLock extends DistributedLock {

    private RedisTemplate redisTemplate;
    /**
     * 需要对 resource上锁
     */
    private String resource;
    /**
     * 资源的值，各个客户端解锁时需要验证
     */
    private String value;

    private Builder<String> lockRandomValueBuilder = new LockRandomValueBuilder();

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock(-1, TimeUnit.MILLISECONDS, true);
    }

    @Override
    public void lock() {
        try {
            lock(-1, TimeUnit.MILLISECONDS, false);
        } catch (InterruptedException ex) {
            // ignore it
        }
    }


    public void lock(long ttl, TimeUnit ttlUnit, boolean interruptibly) throws InterruptedException {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        boolean locked = false;
        while (!locked) {
            locked = doLock(v, ttl, ttlUnit);
            if (!locked) {
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
        }
        if (locked) {
            value = v;
        }
    }

    @Override
    public boolean tryLock() {
        return tryLock(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return tryLock(time, unit, -1, null);
    }

    public boolean tryLock(long lockTime, @Nullable TimeUnit lockUnit, long ttl, @Nullable TimeUnit ttlUnit) {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        lockUnit = lockUnit == null ? TimeUnit.MILLISECONDS : lockUnit;
        ttlUnit = ttlUnit == null ? TimeUnit.MILLISECONDS : ttlUnit;

        boolean locked = false;

        long start = System.currentTimeMillis();
        boolean willWaitWhenLockFail = lockTime > 0;
        long delta = willWaitWhenLockFail ? lockUnit.toMillis(lockTime) : 5L;
        long endTime = start + delta;
        while (!locked && System.currentTimeMillis() < endTime) {
            long lockTime2 = endTime - System.currentTimeMillis();
            if (lockTime2 > 0) {
                locked = doLock(v, ttl, ttlUnit);
            }
            if (!locked && willWaitWhenLockFail) {
                try {
                    synchronized (this) {
                        this.wait(50);
                    }
                } catch (InterruptedException ex) {
                    // ignore it
                }
            }
        }
        if (locked) {
            value = v;
        }
        return locked;
    }

    private boolean doLock(String expectedValue, long ttl, TimeUnit ttlTime) {
        boolean locked = false;
        if (ttl > 0) {
            // 有 过期时间的 Lock
            Object value = redisTemplate.opsForValue().get(resource);
            if (value != null) {
                locked = false;
            } else {
                if (ttlTime == null) {
                    ttlTime = TimeUnit.MICROSECONDS;
                }
                redisTemplate.opsForValue().set(resource, expectedValue, ttl, ttlTime);
                locked = true;
            }
        } else {
            locked = redisTemplate.opsForValue().setIfAbsent(resource, expectedValue);
        }
        return locked;
    }

    public void forceUnlock() {
        unlockOnce(true);
    }

    @Override
    public void unlock() {
        if (value == null) {
            return;
        }
        long start = System.currentTimeMillis();
        long delta = 3 * 1000; // 3s
        long endTime = start + delta;
        boolean unlocked = false;
        while (!unlocked && System.currentTimeMillis() < endTime) {
            unlocked = unlockOnce(false);
            if (!unlocked) {
                try {
                    synchronized (this) {
                        this.wait(50);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }
        // 时间到了，还没解锁成功，强制解锁
        if (!unlocked) {
            unlockOnce(true);
        }
    }

    private boolean unlockOnce(boolean force) {
        boolean unlocked = false;

        if (value == null) {
            redisTemplate.delete(this.resource);
            unlocked = true;
        } else {
            unlocked = (boolean) redisTemplate.executeScript("UnlockExclusiveLock", Collects.newArrayList(this.resource), value, force);
        }
        if (unlocked) {
            value = null;
        }
        return unlocked;
    }


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Builder<String> getLockRandomValueBuilder() {
        return lockRandomValueBuilder;
    }

    public void setLockRandomValueBuilder(Builder<String> lockRandomValueBuilder) {
        if (lockRandomValueBuilder != null) {
            this.lockRandomValueBuilder = lockRandomValueBuilder;
        }
    }
}
