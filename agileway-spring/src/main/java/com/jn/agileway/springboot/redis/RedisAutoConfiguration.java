package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.key.RedisKeyProperties;
import com.jn.agileway.redis.key.RedisKeyWrapper;
import com.jn.agileway.redis.redistemplate.RedisTemplate;
import com.jn.agileway.redis.redistemplate.RedisTemplates;
import com.jn.agileway.redis.redistemplate.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.redistemplate.serialization.EasyjsonRedisSerializer;
import com.jn.agileway.redis.redistemplate.serialization.RedisKeySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(value={
        RedisLuaScriptRepositoryAutoConfiguration.class,
        RedisConnectionFactory.class
})
@ConditionalOnProperty(name = "agileway.redis.global-template.enabled", havingValue = "true", matchIfMissing = false)
public class RedisAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @Bean(name = "globalRedisKeyProperties")
    @ConfigurationProperties(prefix = "agileway.redis.global-template.key")
    public RedisKeyProperties globalRedisKeyProperties() {
        return new RedisKeyProperties();
    }

    @Bean(name = "globalRedisKeyWrapper")
    public RedisKeyWrapper globalRedisKeyWrapper(@Qualifier("globalRedisKeyProperties") RedisKeyProperties globalRedisKeyProperties) {
        return new RedisKeyWrapper(globalRedisKeyProperties);
    }

    @Bean(name = "globalRedisTemplate")
    public RedisTemplate globalRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                             @Qualifier("globalRedisKeyWrapper") RedisKeyWrapper redisKeyWrapper,
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
        logger.info("====== Initial the global redis template ======");
        return redisTemplate;
    }

}
