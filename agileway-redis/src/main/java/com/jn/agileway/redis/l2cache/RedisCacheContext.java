package com.jn.agileway.redis.l2cache;

import com.jn.agileway.redis.redistemplate.key.RedisKeyWrapper;
import com.jn.agileway.redis.redistemplate.IredisTemplate;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Loader;
import com.jn.langx.cache.RemoveListener;

public class RedisCacheContext {
    @NonNull
    private IredisTemplate redisTemplate;
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

    public IredisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(IredisTemplate redisTemplate) {
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
