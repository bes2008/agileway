package com.jn.agileway.redis.locks;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.Builder;
import com.jn.langx.annotation.NotThreadSafe;
import com.jn.agileway.distributed.locks.DistributedLock;
import com.jn.langx.util.collection.Collects;

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
    protected String getKey() {
        return this.resource;
    }

    @Override
    protected Object getValue() {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        return v;
    }

    @Override
    protected void setValue(Object o) {
        this.value = (String) o;
    }

    @Override
    protected boolean doLock(Object o, long ttl, TimeUnit ttlTime) {
        String expectedValue = (String) o;
        boolean locked = false;
        if (ttl > 0) {
            // 有 过期时间的 Lock
            Object value = redisTemplate.opsForValue().get(resource);
            if (value == null) {
                if (ttlTime == null) {
                    ttlTime = TimeUnit.MICROSECONDS;
                }
                if (ttlTime != TimeUnit.MILLISECONDS) {
                    ttl = ttlTime.toMillis(ttl);
                }
                locked = (Boolean) redisTemplate.executeScript("SetIfAbsentWithExpireTime", Collects.newArrayList(this.resource), ttl, value);
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
