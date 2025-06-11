package com.jn.agileway.redis.locks;

import com.jn.agileway.distributed.locks.DistributedLock;
import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.annotation.NotThreadSafe;
import com.jn.langx.util.collection.Collects;

import java.util.concurrent.TimeUnit;

/**
 * https://redis.io/topics/distlock
 */
@NotThreadSafe
public class ExclusiveLock extends DistributedLock {
    private RedisTemplate redisTemplate;

    @Override
    protected boolean doLock(String o, long ttl, TimeUnit ttlTime) {
        String expectedValue = (String) o;
        boolean locked = false;
        if (ttl > 0) {
            // 有 过期时间的 Lock
            Object value = redisTemplate.opsForValue().get(getResource());
            if (value == null) {
                if (ttlTime == null) {
                    ttlTime = TimeUnit.MICROSECONDS;
                }
                if (ttlTime != TimeUnit.MILLISECONDS) {
                    ttl = ttlTime.toMillis(ttl);
                }
                locked = (Boolean) redisTemplate.executeScript("SetIfAbsentWithExpireTime", Collects.newArrayList(getResource()), ttl, value);
            }
        } else {
            locked = redisTemplate.opsForValue().setIfAbsent(getResource(), expectedValue);
        }
        return locked;
    }

    public void forceUnlock() {
        unlockOnce(true);
    }

    @Override
    public void unlock() {
        if (getValue() == null) {
            return;
        }
        long start = System.currentTimeMillis();
        long delta = 3 * 1000L; // 3s
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

        if (getValue() == null) {
            redisTemplate.delete(getResource());
            unlocked = true;
        } else {
            unlocked = (boolean) redisTemplate.executeScript("UnlockExclusiveLock", Collects.newArrayList(getResource()), getValue(), force);
        }
        if (unlocked) {
            setValue(null);
        }
        return unlocked;
    }


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


}
