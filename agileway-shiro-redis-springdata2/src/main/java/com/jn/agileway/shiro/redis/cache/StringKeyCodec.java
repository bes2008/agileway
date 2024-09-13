package com.jn.agileway.shiro.redis.cache;

/**
 * Redis 里的Key 必然是 String的，如果期望使用的缓存Key不是字符串，则需要自定义编解码
 * @param <T>
 */
public interface StringKeyCodec<T> {
    String encode(T obj);
    T decode(String string);
}
