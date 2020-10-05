package com.jn.agileway.redis.locks;

import com.jn.agileway.redis.redistemplate.IredisTemplate;
import com.jn.langx.Builder;
import com.jn.langx.annotation.NotThreadSafe;
import com.jn.langx.util.collection.Collects;

import java.util.concurrent.TimeUnit;

@NotThreadSafe
public class ExclusiveLock extends DistributedLock {

    private IredisTemplate redisTemplate;
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
    public void lock() {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        boolean locked = false;
        while (!locked) {
            locked = redisTemplate.opsForValue().setIfAbsent(resource, v);
            if (!locked) {
                try {
                    this.wait(50);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        }
        value = v;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        boolean locked = false;
        while (!locked) {
            locked = redisTemplate.opsForValue().setIfAbsent(resource, v);
            if (!locked) {
                try {
                    this.wait(50);
                } catch (InterruptedException ex) {
                    throw ex;
                }
            }
        }
        value = v;
    }

    @Override
    public boolean tryLock() {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        boolean locked = redisTemplate.opsForValue().setIfAbsent(resource, v);
        if (locked) {
            value = v;
        }
        return locked;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        String v = value;
        if (v == null) {
            v = lockRandomValueBuilder.build();
        }
        boolean locked = false;
        long start = System.currentTimeMillis();
        boolean willWaitWhenLockFail = time > 0;
        long delta = unit.toMillis(time);
        long endTime = start + delta;
        while (!locked && System.currentTimeMillis() < endTime) {
            locked = redisTemplate.opsForValue().setIfAbsent(resource, v);
            if (!locked && willWaitWhenLockFail) {
                try {
                    this.wait(50);
                } catch (InterruptedException ex) {
                    throw ex;
                }
            }
        }
        if (locked) {
            value = v;
        }
        return locked;
    }


    @Override
    public void unlock() {
        if (value == null) {
            return;
        }
        long start = System.currentTimeMillis();
        long delta = 60 * 1000; // 1 min
        long endTime = start + delta;
        boolean unlocked = false;
        while (!unlocked && System.currentTimeMillis() < endTime) {
            unlocked = unlockOnce(false);
            if (!unlocked) {
                try {
                    this.wait(50);
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
        if (value != null) {
            boolean unlocked = (boolean) redisTemplate.executeScript("UnlockExclusiveLock", Collects.newArrayList(this.resource), value, force);
            if (unlocked) {
                value = null;
            }
            return unlocked;
        }
        return true;
    }


    public IredisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(IredisTemplate redisTemplate) {
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
