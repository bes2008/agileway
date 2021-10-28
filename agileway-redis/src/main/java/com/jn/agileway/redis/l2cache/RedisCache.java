package com.jn.agileway.redis.l2cache;

import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.*;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.*;
import com.jn.langx.util.timing.timer.Timeout;
import org.springframework.data.redis.core.BoundValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisCache<V> extends BaseCache<String, V> {
    @NonNull
    private RedisTemplate<String, V> redisTemplate;
    @Nullable
    private Loader<String, V> loader;
    // unit: seconds
    @Nullable
    private RemoveListener removeListener = RemoveListeners.noop();

    @NonNull
    private RedisKeyWrapper keyWrapper = new RedisKeyWrapper().prefix("iredis:cache");

    @Override
    public void set(String key, V value) {
        BoundValueOperations<String, V> valueOperations = redisTemplate.boundValueOps(keyWrapper.wrap(key));
        valueOperations.set(value);
    }

    @Override
    public void set(String key, V value, long expire) {
        long now = System.currentTimeMillis();
        long ttl = expire - now;
        set(key, value, ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void set(String key, V value, long ttl, TimeUnit timeUnit) {
        BoundValueOperations<String, V> valueOperations = redisTemplate.boundValueOps(keyWrapper.wrap(key));
        if (ttl < 0) {
            valueOperations.set(value);
        }
        if (ttl > 0) {
            valueOperations.set(value, ttl, timeUnit);
        }
    }

    @Override
    public V get(String key) {
        V v = getFromRedis(key);
        if (v == null && loader != null) {
            v = loader.load(key);
            if (v != null) {
                set(key, v);
            }
        }
        return v;
    }

    private V getFromRedis(String unwrappedKey) {
        V v = null;
        BoundValueOperations<String, V> valueOperations = redisTemplate.boundValueOps(keyWrapper.wrap(unwrappedKey));
        v = valueOperations.get();
        return v;
    }

    private V getAndUpdateExpireTime(String key, long durationMills) {
        return redisTemplate.executeScript("GetAndUpdateExpireTime", Collects.asList(keyWrapper.wrap(key)), durationMills);
    }

    @Override
    public Map<String, V> getAll(Iterable<String> keys) {
        List<String> keyList = keyWrapper.wrap(keys);
        final List<V> values = redisTemplate.opsForValue().multiGet(keyList);
        final Map<String, V> map = new HashMap<String, V>();
        if (Emptys.isNotEmpty(values)) {
            Collects.forEach(keyList, new Consumer2<Integer, String>() {
                @Override
                public void accept(Integer index, String wrappedKey) {
                    String unwrappedKey = keyWrapper.unwrap(wrappedKey);
                    V v = values.get(index);
                    if (v == null && loader != null) {
                        v = loader.load(unwrappedKey);
                        if (v != null) {
                            set(unwrappedKey, v);
                        }
                    }
                    map.put(unwrappedKey, v);
                }
            });
        }
        return map;
    }

    @Override
    public Map<String, V> getAllIfPresent(Iterable<String> keys) {
        List<String> keyList = Collects.asList(keys);
        final List<V> values = redisTemplate.opsForValue().multiGet(keyList);
        final Map<String, V> map = new HashMap<String, V>();
        Collects.forEach(keyList, new Consumer2<Integer, String>() {
            @Override
            public void accept(Integer index, String key) {
                V v = values.get(index);
                if (v != null) {
                    map.put(key, v);
                }
            }
        });
        return map;
    }

    @Override
    public V getIfPresent(String key) {
        BoundValueOperations<String, V> valueOperations = redisTemplate.boundValueOps(key);
        return valueOperations.get();
    }

    @Override
    public V get(String key, Supplier<String, V> loader) {
        V value = getFromRedis(key);
        if (value == null) {
            if (loader != null) {
                value = loader.get(key);
            }
            if (value == null && this.loader != null) {
                value = this.loader.load(key);
            }
        }
        if (value != null) {
            set(key, value);
        }
        return null;
    }

    @Override
    public V remove(String key) {
        V ret = redisTemplate.executeScript("GetAndRemove", Collects.asList(keyWrapper.wrap(key)));
        removeListener.onRemove(key, ret, RemoveCause.EXPLICIT);
        return ret;
    }

    @Override
    public List<V> remove(Collection<String> keys) {
        return redisTemplate.executeScript("mGetAndRemove", keyWrapper.wrap(keys));
    }

    @Override
    public void refresh(String key) {
        if (loader != null) {
            V v = loader.load(key);
            if (v == null) {
                redisTemplate.delete(keyWrapper.wrap(key));
            } else {
                set(key, v);
                removeListener.onRemove(key, v, RemoveCause.REPLACED);
            }
        }
    }

    @Override
    protected void refreshAllAsync(@Nullable final Timeout timeout) {
        Set<String> keys = this.keys();
        Pipeline.of(keys)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String wrappedKey) {
                        return keyWrapper.unwrap(wrappedKey);
                    }
                }).forEach(new Consumer<String>() {
                    public void accept(String key) {
                        refresh(key);
                    }
                });
    }

    @Override
    public void evictExpired() {
        // noop
    }

    @Override
    public Set<String> keys() {
        return redisTemplate.keys(keyWrapper.wrap("*"));
    }

    @Override
    public void clean() {
        Set<String> wrappedKeys = keys();
        redisTemplate.delete(wrappedKeys);
    }

    @Override
    public int size() {
        Set<String> wrappedKeys = keys();
        return com.jn.langx.util.Objects.length(wrappedKeys);
    }

    @Override
    public Map<String, V> toMap() {
        Set<String> wrappedKeys = keys();
        final List<V> values = redisTemplate.opsForValue().multiGet(wrappedKeys);
        final Map<String, V> map = Collects.<String, V>emptyHashMap();
        Collects.forEach(wrappedKeys, new Consumer2<Integer, String>() {
            @Override
            public void accept(Integer index, String wrappedKey) {
                String unwrappedKey = keyWrapper.unwrap(wrappedKey);
                map.put(unwrappedKey, values.get(index));
            }
        });
        return map;
    }

    public void setRemoveListener(RemoveListener removeListener) {
        if (removeListener != null) {
            this.removeListener = removeListener;
        }
    }

    public RedisTemplate<String, V> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Loader<String, V> getLoader() {
        return loader;
    }

    public void setLoader(Loader<String, V> loader) {
        this.loader = loader;
    }

    public RemoveListener getRemoveListener() {
        return removeListener;
    }

    public RedisKeyWrapper getKeyWrapper() {
        return keyWrapper;
    }

    public void setKeyWrapper(RedisKeyWrapper keyWrapper) {
        if (keyWrapper != null) {
            this.keyWrapper = keyWrapper;
        }
    }
}
