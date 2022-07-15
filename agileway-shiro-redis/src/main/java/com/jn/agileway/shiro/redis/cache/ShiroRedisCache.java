package com.jn.agileway.shiro.redis.cache;

import com.jn.agileway.redis.l2cache.RedisCache;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * 基于 RedisCache 来实现Shiro里的Cache接口，由于Redis 中的key都是字符串的，Shiro 的 Cache API中的Key是任意的，
 * 所以增加了 Key 转换功能
 * @see StringKeyCodec
 * @param <K>
 * @param <V>
 */
public class ShiroRedisCache<K, V> implements Cache<K, V> {
    private RedisCache<V> redisCache;
    private StringKeyCodec<K> keyCodec = (StringKeyCodec<K>) new NoopStringKeyCodec();

    @Override
    public V get(K key) throws CacheException {
        String redisKey = keyCodec.encode(key);
        if (Strings.isNotBlank(redisKey)) {
            return redisCache.get(redisKey);
        } else {
            throw new CacheException(StringTemplates.formatWithPlaceholder("invalid key: {}", key));
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        String redisKey = keyCodec.encode(key);
        if (Strings.isNotBlank(redisKey)) {
            return redisCache.get(redisKey);
        } else {
            throw new CacheException(StringTemplates.formatWithPlaceholder("invalid key: {}", key));
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        String redisKey = keyCodec.encode(key);
        if (Strings.isNotBlank(redisKey)) {
            return redisCache.remove(redisKey);
        } else {
            throw new CacheException(StringTemplates.formatWithPlaceholder("invalid key: {}", key));
        }
    }

    @Override
    public void clear() throws CacheException {
        redisCache.clean();
    }

    @Override
    public int size() {
        return redisCache.size();
    }

    @Override
    public Set<K> keys() {
        Set<String> wrappedKeys = redisCache.getRedisTemplate().keys(redisCache.getKeyWrapper().wrap("*"));
        return Collects.asSet(Collects.map(wrappedKeys, new Function<String, K>() {
            @Override
            public K apply(String encodedKey) {
                encodedKey = redisCache.getKeyWrapper().unwrap(encodedKey);
                return keyCodec.decode(encodedKey);
            }
        }), true);
    }

    @Override
    public Collection<V> values() {
        return redisCache.toMap().values();
    }

    public RedisCache<V> getRedisCache() {
        return redisCache;
    }

    public void setRedisCache(RedisCache<V> redisCache) {
        this.redisCache = redisCache;
    }

    public StringKeyCodec<K> getKeyCodec() {
        return keyCodec;
    }

    public void setKeyCodec(StringKeyCodec<K> keyCodec) {
        if(keyCodec!=null) {
            this.keyCodec = keyCodec;
        }
    }

}
