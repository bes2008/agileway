package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.agileway.redis.core.RedisTemplates;
import com.jn.agileway.redis.core.key.RedisKeyProperties;
import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.core.serialization.DelegatableRedisSerializer;
import com.jn.agileway.redis.core.serialization.RedisKeySerializer;
import com.jn.agileway.codec.json.EasyjsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(value = {
        RedisLuaScriptRepositoryAutoConfiguration.class,
        RedisConnectionFactory.class
})
@ConditionalOnProperty(name = "agileway.redis.global-template.enabled", havingValue = "true", matchIfMissing = false)
public class RedisGlobalTemplateAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisGlobalTemplateAutoConfiguration.class);

    @ConditionalOnMissingBean(name = "globalRedisKeyProperties")
    @Bean(name = "globalRedisKeyProperties")
    @ConfigurationProperties(prefix = "agileway.redis.global-template.key")
    public RedisKeyProperties globalRedisKeyProperties() {
        return new RedisKeyProperties();
    }

    @ConditionalOnMissingBean(name = "globalRedisKeyWrapper")
    @Bean(name = "globalRedisKeyWrapper")
    @Autowired
    public RedisKeyWrapper globalRedisKeyWrapper(@Qualifier("globalRedisKeyProperties") RedisKeyProperties globalRedisKeyProperties) {
        return new RedisKeyWrapper(globalRedisKeyProperties);
    }

    @ConditionalOnMissingBean(name = "globalRedisTemplate")
    @Bean(name = "globalRedisTemplate")
    @Autowired
    public RedisTemplate globalRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                             @Qualifier("globalRedisKeyWrapper") RedisKeyWrapper redisKeyWrapper,
                                             RedisLuaScriptRepository luaScriptRepository) {
        EasyjsonCodec valueSerializer = new EasyjsonCodec<>();
        valueSerializer.setSerializeType(true);

        DelegatableRedisSerializer redisValueSerializer = new DelegatableRedisSerializer(valueSerializer);
        RedisTemplate redisTemplate = RedisTemplates.createRedisTemplate(redisConnectionFactory,
                redisKeyWrapper,
                redisValueSerializer,
                this.getClass().getClassLoader(),
                null,
                new RedisKeySerializer(),
                redisValueSerializer,
                luaScriptRepository,
                false, true
        );
        logger.info("====== Initial the global redis template ======");
        return redisTemplate;
    }

}
