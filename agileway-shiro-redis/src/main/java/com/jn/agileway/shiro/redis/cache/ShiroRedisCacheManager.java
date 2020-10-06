package com.jn.agileway.shiro.redis.cache;

import com.jn.agileway.redis.l2cache.RedisCache;
import com.jn.agileway.redis.l2cache.RedisCacheContext;
import com.jn.agileway.redis.l2cache.RedisCacheFactory;
import com.jn.agileway.redis.redistemplate.RedisTemplate;
import com.jn.agileway.redis.key.RedisKeyWrapper;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Loader;
import com.jn.langx.cache.RemoveListener;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

/**
 * 借助于 RedisCacheFactory 来创建的 Shiro的 基于redis 的cache
 */
public class ShiroRedisCacheManager extends AbstractCacheManager {

    @NonNull
    private RedisTemplate redisTemplate;
    @Nullable
    private Loader<String, ?> loader;
    @Nullable
    private RemoveListener removeListener;

    @Nullable
    private StringKeyCodec keyCodec;

    @Override
    protected Cache createCache(String cacheName) throws CacheException {
        RedisKeyWrapper redisCacheKeyWrapper = new RedisKeyWrapper().prefix("shiro_redis:" + cacheName);
        RedisCacheContext cacheContext = new RedisCacheContext();
        cacheContext.setLoader(loader);
        cacheContext.setRedisTemplate(redisTemplate);
        cacheContext.setKeyWrapper(redisCacheKeyWrapper);
        cacheContext.setRemoveListener(removeListener);

        RedisCache redisCache = new RedisCacheFactory().get(cacheContext);

        ShiroRedisCache shiroRedisCache = new ShiroRedisCache();
        shiroRedisCache.setRedisCache(redisCache);
        shiroRedisCache.setKeyCodec(keyCodec);
        return shiroRedisCache;
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

    public StringKeyCodec getKeyCodec() {
        return keyCodec;
    }

    public void setKeyCodec(StringKeyCodec keyCodec) {
        this.keyCodec = keyCodec;
    }
}
