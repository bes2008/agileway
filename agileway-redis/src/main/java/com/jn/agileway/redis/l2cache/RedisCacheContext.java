package com.jn.agileway.redis.l2cache;

import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Loader;
import com.jn.langx.cache.RemoveListener;

public class RedisCacheContext {
    @NonNull
    private RedisTemplate redisTemplate;
    @Nullable
    private Loader<String, ?> loader;
    @Nullable
    private RemoveListener removeListener;
    private RedisKeyWrapper keyWrapper;

    public RedisKeyWrapper getKeyWrapper() {
        return keyWrapper;
    }

    public void setKeyWrapper(RedisKeyWrapper keyWrapper) {
        this.keyWrapper = keyWrapper;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Loader<String, ?> getLoader() {
        return loader;
    }

    public void setLoader(Loader<String, ?> loader) {
        this.loader = loader;
    }

    public RemoveListener getRemoveListener() {
        return removeListener;
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }
}
