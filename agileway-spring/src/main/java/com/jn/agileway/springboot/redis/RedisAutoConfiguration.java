package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.key.RedisKeyProperties;
import com.jn.agileway.redis.key.RedisKeyWrapper;
import com.jn.agileway.redis.redistemplate.RedisTemplate;
import com.jn.agileway.redis.redistemplate.RedisTemplates;
import com.jn.agileway.redis.redistemplate.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.redistemplate.serialization.EasyjsonRedisSerializer;
import com.jn.agileway.redis.redistemplate.serialization.RedisKeySerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisAutoConfiguration {

    @Bean(name = "globalRedisKeyProperties")
    @ConfigurationProperties(prefix = "spring.redis.global")
    public RedisKeyProperties globalRedisKeyProperties() {
        return new RedisKeyProperties();
    }

    @Bean(name = "globalRedisKeyWrapper")
    public RedisKeyWrapper globalRedisKeyWrapper(@Qualifier("globalRedisKeyProperties") RedisKeyProperties globalRedisKeyProperties) {
        return new RedisKeyWrapper(globalRedisKeyProperties);
    }

    @Bean
    public RedisLuaScriptRepository redisLuaScriptRepository() {
        RedisLuaScriptRepository repository = new RedisLuaScriptRepository();
        repository.init();
        return repository;
    }

    @Bean(name = "globalRedisTemplate")
    public RedisTemplate globalRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                             @Qualifier("licenseGlobalRedisKeyWrapper") RedisKeyWrapper redisKeyWrapper,
                                             RedisLuaScriptRepository luaScriptRepository) {
        EasyjsonRedisSerializer valueSerializer = new EasyjsonRedisSerializer<>();
        valueSerializer.setSerializeType(true);
        RedisTemplate redisTemplate = RedisTemplates.createRedisTemplate(redisConnectionFactory,
                redisKeyWrapper,
                valueSerializer,
                this.getClass().getClassLoader(),
                null,
                new RedisKeySerializer(),
                valueSerializer,
                luaScriptRepository,
                false, true
        );
        return redisTemplate;
    }

}
