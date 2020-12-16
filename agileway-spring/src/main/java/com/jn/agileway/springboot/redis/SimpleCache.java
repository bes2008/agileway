package com.jn.agileway.springboot.redis;

import com.jn.langx.cache.AbstractCache;
import com.jn.langx.cache.Entry;
import com.jn.langx.cache.RemoveCause;

import java.util.List;

/**
 * 不过期的缓存
 * @param <K>
 * @param <V>
 */
public class SimpleCache<K,V> extends AbstractCache<K,V> {

    public SimpleCache(){
        this(Integer.MAX_VALUE, Long.MAX_VALUE);
    }

    protected SimpleCache(int maxCapacity, long evictExpiredInterval) {
        super(maxCapacity, evictExpiredInterval, null);
    }


    @Override
    protected void addToCache(Entry<K, V> entry) {
        // NOOP
    }

    @Override
    protected void beforeRecomputeExpireTimeOnRead(Entry<K, V> entry) {
        // NOOP
    }

    @Override
    protected void afterRecomputeExpireTimeOnRead(Entry<K, V> entry) {
        // NOOP
    }

    @Override
    protected void removeFromCache(Entry<K, V> entry, RemoveCause removeCause) {
        // NOOP
    }

    @Override
    protected void beforeRead(Entry<K, V> entry) {
        // NOOP
    }

    @Override
    protected void afterRead(Entry<K, V> entry) {
        // NOOP
    }

    @Override
    protected List<K> forceEvict(int count) {
        return null;
    }
}
