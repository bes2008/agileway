package com.jn.agileway.redis.redistemplate;

import com.jn.agileway.redis.redistemplate.key.RedisKeyProperties;
import com.jn.agileway.redis.redistemplate.key.RedisKeyWrapper;
import com.jn.agileway.redis.redistemplate.serialization.EasyjsonRedisSerializer;
import com.jn.agileway.redis.redistemplate.serialization.RedisKeySerializer;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.agileway.redis.redistemplate.script.RedisLuaScriptRepository;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisTemplates {

    public static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    public static RedisTemplate<String, ?> createBeanJsonRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @NonNull Class beanClass,
            @Nullable RedisSerializer hashKeySerializer,
            @Nullable RedisSerializer hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        Preconditions.checkNotNull(beanClass, "the target class is null");
        EasyjsonRedisSerializer easyjsonRedisSerializer = new EasyjsonRedisSerializer();
        easyjsonRedisSerializer.setJsonFactory(JsonFactorys.getJSONFactory(JsonScope.SINGLETON));
        easyjsonRedisSerializer.setTargetType(beanClass);
        return createRedisTemplate(connectionFactory, keyPrefix, easyjsonRedisSerializer, beanClass.getClassLoader(), null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }

    public static RedisTemplate<String, ?> createBeanRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @NonNull Class<?> beanClass,
            @NonNull RedisSerializer<?> beanSerializer,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        Preconditions.checkNotNull(beanClass, "the bean class is null");
        Preconditions.checkNotNull(beanSerializer, "the bean serializer is null");
        return createRedisTemplate(connectionFactory, keyPrefix, beanSerializer, beanClass.getClassLoader(), null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }

    public static RedisTemplate<String, ?> createEasyjsonCommonJsonRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @NonNull JSONFactory jsonFactory,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        EasyjsonRedisSerializer easyjsonRedisSerializer = new EasyjsonRedisSerializer();
        easyjsonRedisSerializer.setJsonFactory(jsonFactory);
        return createRedisTemplate(connectionFactory, keyPrefix, easyjsonRedisSerializer, beanClassLoader, null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }

    public static RedisTemplate<String, ?> createJacksonCommonJsonRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        RedisSerializer<?> jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        return createRedisTemplate(connectionFactory, keyPrefix, jsonRedisSerializer, beanClassLoader, null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }


    public static RedisTemplate<String, ?> createRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @NonNull RedisSerializer<?> valueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        return createRedisTemplate(connectionFactory, keyPrefix, valueSerializer, null, null, null, null, redisLuaScriptRepository, enableTx,initIt);
    }


    public static RedisTemplate<String, ?> createRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable String keyPrefix,
            @NonNull RedisSerializer<?> valueSerializer,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisSerializer<String> stringSerializer,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        RedisKeySerializer redisKeySerializer = new RedisKeySerializer();
        RedisKeyProperties keyProperties = new RedisKeyProperties();
        keyProperties.setPrefix(keyPrefix);
        RedisKeyWrapper keyWrapper = new RedisKeyWrapper(keyProperties);
        redisKeySerializer.setKeyWrapper(keyWrapper);
        return createRedisTemplate(connectionFactory, redisKeySerializer, valueSerializer, beanClassLoader, stringSerializer, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx,initIt);
    }

    public static RedisTemplate<String, ?> createRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable RedisSerializer<String> keySerializer,
            @NonNull RedisSerializer<?> valueSerializer,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisSerializer<String> stringSerializer,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate<String, Object>();
        Preconditions.checkNotNull(connectionFactory, "the redis connection factory is null");
        redisTemplate.setConnectionFactory(connectionFactory);

        keySerializer = keySerializer == null ? stringRedisSerializer : keySerializer;
        redisTemplate.setKeySerializer(keySerializer);

        Preconditions.checkNotNull(valueSerializer, "the value serializer is null");
        redisTemplate.setValueSerializer(valueSerializer);

        if (beanClassLoader != null) {
            redisTemplate.setBeanClassLoader(beanClassLoader);
        }

        stringSerializer = stringSerializer == null ? stringRedisSerializer : stringSerializer;
        redisTemplate.setStringSerializer(stringSerializer);

        redisTemplate.setHashKeySerializer(hashKeySerializer);
        redisTemplate.setHashValueSerializer(hashValueSerializer);

        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.setExposeConnection(true);

        redisTemplate.setLuaScriptRepository(redisLuaScriptRepository);

        redisTemplate.setEnableTransactionSupport(enableTx);

        if(initIt) {
            redisTemplate.afterPropertiesSet();
        }
        return redisTemplate;
    }
}
