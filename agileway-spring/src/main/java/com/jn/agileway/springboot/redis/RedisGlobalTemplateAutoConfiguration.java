package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.agileway.redis.core.RedisTemplates;
import com.jn.agileway.redis.core.conf.RedisTemplateProperties;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@ConditionalOnClass({RedisLuaScriptRepositoryAutoConfiguration.class, RedisConnectionFactory.class})
@Configuration
@AutoConfigureAfter(value = {
        RedisLuaScriptRepositoryAutoConfiguration.class,
        RedisConnectionFactory.class
})
@ConditionalOnProperty(name = "agileway.redis.global-template.enabled", havingValue = "true", matchIfMissing = false)
public class RedisGlobalTemplateAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisGlobalTemplateAutoConfiguration.class);

    @ConditionalOnMissingBean(name = "globalRedisTemplateProperties")
    @Bean(name = "globalRedisTemplateProperties")
    @ConfigurationProperties(prefix = "agileway.redis.global-template")
    public RedisTemplateProperties globalRedisTemplateProperties() {
        return new RedisTemplateProperties();
    }


    @ConditionalOnMissingBean(name = "globalRedisTemplate")
    @Bean(name = "globalRedisTemplate")
    @Autowired
    public RedisTemplate globalRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                             @Qualifier("globalRedisTemplateProperties")
                                                     RedisTemplateProperties globalRedisTemplateProperties,
                                             RedisLuaScriptRepository luaScriptRepository) {

        RedisTemplate redisTemplate = RedisTemplates.createRedisTemplate(redisConnectionFactory,
                globalRedisTemplateProperties,
                this.getClass().getClassLoader(),
                luaScriptRepository,
                true
        );
        logger.info("===[AGILE_WAY-GLOBAL_REDIS_TEMPLATE]=== Initial the global redis template");
        logger.info("the global redis template configuration: {}", globalRedisTemplateProperties);
        return redisTemplate;
    }

}
