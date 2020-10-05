package com.jn.agileway.redis.redistemplate.script;

import com.jn.langx.configuration.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

public class RedisLuaScript<T> extends DefaultRedisScript<T> implements Configuration {
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
