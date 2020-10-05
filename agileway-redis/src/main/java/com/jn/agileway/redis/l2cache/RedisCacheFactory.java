package com.jn.agileway.redis.l2cache;

import com.jn.langx.factory.Factory;

public class RedisCacheFactory implements Factory<RedisCacheContext, RedisCache> {
    @Override
    public RedisCache get(RedisCacheContext context) {
        RedisCache redisCache = new RedisCache();
        redisCache.setLoader(context.getLoader());
        redisCache.setRedisTemplate(context.getRedisTemplate());
        redisCache.setRemoveListener(context.getRemoveListener());
        redisCache.setKeyWrapper(context.getKeyWrapper());
        return redisCache;
    }
}
