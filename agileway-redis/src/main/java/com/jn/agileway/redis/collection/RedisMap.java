package com.jn.agileway.redis.collection;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RedisMap<K, V> implements Map<K, V> {
    private BoundHashOperations ops;
    private String key;

    public RedisMap(RedisOperations<String, Map<K, V>> redisTemplate, String key) {
        this.ops = redisTemplate.boundHashOps(key);
        this.key = key;
    }

    @Override
    public int size() {
        return (int)longSize();
    }

    public long longSize() {
        return ops.size();
    }

    @Override
    public boolean isEmpty() {
        return longSize() == 0L;
    }

    @Override
    public boolean containsKey(Object key) {
        return ops.hasKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return ops.values().contains(value);
    }

    @Override
    public V get(Object key) {
        return (V) ops.get(key);
    }

    @Override
    public V put(K key, V value) {
        V old = get(key);
        ops.put(key, value);
        return old;
    }

    @Override
    public V remove(Object key) {
        V old = get(key);
        ops.delete(key);
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ops.putAll(m);
    }

    @Override
    public void clear() {
        ops.delete(ops.keys());
    }

    @Override
    public Set<K> keySet() {
        return ops.keys();
    }

    @Override
    public Collection<V> values() {
        return ops.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return ops.entries().entrySet();
    }
}
