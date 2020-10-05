package com.jn.agileway.redis.counter;

import com.jn.agileway.redis.redistemplate.IredisTemplate;
import com.jn.langx.util.struct.counter.Counter;

public interface DistributedCounter extends Counter<Long> {
    void clear();

    IredisTemplate getRedisTemplate();

    void setRedisTemplate(IredisTemplate redisTemplate);

    String getCounterKey();

    void setCounterKey(String key);

    String getKey();

    void setKey(String key);
}
