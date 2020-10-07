package com.jn.agileway.redis.core;

import com.jn.agileway.redis.core.script.RedisLuaScript;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

public class RedisTemplate<K, V> extends org.springframework.data.redis.core.RedisTemplate<K, V> {
    private RedisLuaScriptRepository luaScriptRepository;

    public RedisLuaScript findRedisScript(String scriptId) {
        if (luaScriptRepository != null) {
            return luaScriptRepository.getById(scriptId);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.RedisOperations#execute(org.springframework.data.redis.core.script.RedisScript, java.util.List, java.lang.Object[])
     */
    public <T> T executeScript(String scriptId, List<K> keys, Object... args) {
        RedisLuaScript<T> redisLuaScript = findRedisScript(scriptId);
        if (redisLuaScript != null) {
            return execute(redisLuaScript, keys, args);
        }
        return null;
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.RedisOperations#execute(org.springframework.data.redis.core.script.RedisScript, org.springframework.data.redis.serializer.RedisSerializer, org.springframework.data.redis.serializer.RedisSerializer, java.util.List, java.lang.Object[])
     */
    public <T> T executeScript(String scriptId, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer, List<K> keys, Object... args) {
        RedisLuaScript<T> redisLuaScript = findRedisScript(scriptId);
        if (redisLuaScript != null) {
            return execute(redisLuaScript, argsSerializer, resultSerializer, keys, args);
        }
        return null;
    }


    public RedisLuaScriptRepository getLuaScriptRepository() {
        return luaScriptRepository;
    }

    public void setLuaScriptRepository(RedisLuaScriptRepository luaScriptRepository) {
        this.luaScriptRepository = luaScriptRepository;
    }
}
