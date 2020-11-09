package com.jn.agileway.redis.counter;

import com.jn.agileway.redis.core.RedisTemplate;

public class SimpleDistributedCounter implements DistributedCounter {
    private RedisTemplate redisTemplate;
    private String key;

    public SimpleDistributedCounter(RedisTemplate redisTemplate, String key) {
        setRedisTemplate(redisTemplate);
        setCounterKey(key);
    }

    @Override
    public Long increment() {
        return increment(1L);
    }

    @Override
    public Long increment(Long delta) {
        return redisTemplate.opsForValue().increment(this.key, delta);
    }

    @Override
    public Long decrement() {
        return decrement(1L);
    }

    @Override
    public Long decrement(Long delta) {
        return redisTemplate.opsForValue().increment(this.key, -delta);
    }

    @Override
    public Long get() {
        return (Long) redisTemplate.opsForValue().get(this.key);
    }

    @Override
    public void set(Long aLong) {
        redisTemplate.opsForValue().set(this.key, aLong);
    }

    @Override
    public void clear() {
        redisTemplate.delete(this.key);
    }

    @Override
    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    @Override
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCounterKey() {
        return this.key;
    }

    @Override
    public void setCounterKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }
}
