package com.jn.agileway.springboot.redis;

import com.jn.agileway.redis.core.script.BuiltinLuaScriptLocationProvider;
import com.jn.agileway.redis.core.script.RedisLuaScriptParser;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.core.script.RedisLuaScriptResourceLoader;
import com.jn.langx.configuration.resource.ResourceConfigurationLoader;
import com.jn.langx.io.resource.ResourceLocationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(RedisConnectionFactory.class)
public class RedisLuaScriptRepositoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisLuaScriptParser")
    public RedisLuaScriptParser redisLuaScriptParser() {
        return new RedisLuaScriptParser();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisLuaScriptLocationProvider")
    public ResourceLocationProvider redisLuaScriptLocationProvider() {
        BuiltinLuaScriptLocationProvider provider = new BuiltinLuaScriptLocationProvider();
        provider.init();
        return provider;
    }

    @Bean(name = "redisLuaScriptLoader")
    @ConditionalOnMissingBean(name = "redisLuaScriptLoader")
    @Autowired
    public ResourceConfigurationLoader redisLuaScriptLoader(
            @Qualifier("redisLuaScriptParser") RedisLuaScriptParser parser,
            @Qualifier("redisLuaScriptLocationProvider") ResourceLocationProvider redisLuaScriptLocationProvider) {
        ResourceConfigurationLoader loader = new ResourceConfigurationLoader<>();
        loader.setParser(parser);
        loader.setResourceLocationProvider(redisLuaScriptLocationProvider);
        return loader;
    }


    @Bean
    @ConditionalOnMissingBean(name = "redisLuaScriptRepository")
    @Autowired
    public RedisLuaScriptRepository redisLuaScriptRepository(
            @Qualifier("redisLuaScriptLoader")
                    ResourceConfigurationLoader redisLuaScriptLoader) {
        RedisLuaScriptRepository repository = new RedisLuaScriptRepository();
        repository.setName("Redis-LUA-Script-Repository");
        repository.setConfigurationLoader(new RedisLuaScriptResourceLoader());
        repository.setConfigurationLoader(redisLuaScriptLoader);
        repository.init();
        return repository;
    }
}
