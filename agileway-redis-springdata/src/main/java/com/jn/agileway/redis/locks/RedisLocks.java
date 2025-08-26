package com.jn.agileway.redis.locks;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.Builder;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;

public class RedisLocks {

    public static ExclusiveLock newExclusiveLock(@NonNull RedisTemplate redisTemplate, @NonNull String resourceKey) {
        return newExclusiveLock(redisTemplate, resourceKey, null);
    }

    public static ExclusiveLock newExclusiveLock(@NonNull RedisTemplate redisTemplate, @NonNull String resourceKey, @Nullable Builder<String> randomValueBuilder) {
        Preconditions.checkNotNull(redisTemplate, "the redisTemplate is null");
        Preconditions.checkNotNull(resourceKey, "the resource is null");
        ExclusiveLock lock = new ExclusiveLock();
        lock.setRedisTemplate(redisTemplate);
        lock.setResource(resourceKey);
        if (randomValueBuilder != null) {
            lock.setLockRandomValueBuilder(randomValueBuilder);
        }
        return lock;
    }
}
