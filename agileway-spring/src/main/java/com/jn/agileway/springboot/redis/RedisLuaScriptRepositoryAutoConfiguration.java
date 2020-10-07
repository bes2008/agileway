package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.redistemplate.script.RedisLuaScriptRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(value = {
        RedisConnectionFactory.class
})
public class RedisLuaScriptRepositoryAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "redisLuaScriptRepository")
    public RedisLuaScriptRepository redisLuaScriptRepository() {
        RedisLuaScriptRepository repository = new RedisLuaScriptRepository();
        repository.setName("Redis-LUA-Script-Repository");
        repository.init();
        return repository;
    }
}
