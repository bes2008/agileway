package com.jn.agileway.redis.counter;

import com.jn.agileway.redis.redistemplate.RedisTemplate;
import com.jn.langx.util.struct.counter.Counter;

public interface DistributedCounter extends Counter<Long> {
    void clear();

    RedisTemplate getRedisTemplate();

    void setRedisTemplate(RedisTemplate redisTemplate);

    String getCounterKey();

    void setCounterKey(String key);

    String getKey();

    void setKey(String key);
}
