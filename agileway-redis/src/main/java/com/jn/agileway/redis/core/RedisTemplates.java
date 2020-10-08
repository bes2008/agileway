package com.jn.agileway.redis.core;

import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.core.serialization.DelegatableRedisSerializer;
import com.jn.agileway.redis.core.serialization.RedisKeySerializer;
import com.jn.agileway.serialization.GenericSerializer;
import com.jn.agileway.serialization.json.EasyjsonGenericSerializer;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
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
        EasyjsonGenericSerializer easyjsonRedisSerializer = new EasyjsonGenericSerializer();
        easyjsonRedisSerializer.setJsonFactory(JsonFactorys.getJSONFactory(JsonScope.SINGLETON));
        easyjsonRedisSerializer.setTargetType(beanClass);
        return createRedisTemplate(connectionFactory, keyPrefix, new DelegatableRedisSerializer(easyjsonRedisSerializer), beanClass.getClassLoader(), null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
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
        EasyjsonGenericSerializer easyjsonRedisSerializer = new EasyjsonGenericSerializer();
        easyjsonRedisSerializer.setJsonFactory(jsonFactory);
        return createRedisTemplate(connectionFactory, keyPrefix, new DelegatableRedisSerializer(easyjsonRedisSerializer), beanClassLoader, null, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
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
        return createRedisTemplate(connectionFactory, keyPrefix, valueSerializer, null, null, null, null, redisLuaScriptRepository, enableTx, initIt);
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
        RedisKeyWrapper keyWrapper = new RedisKeyWrapper().prefix(keyPrefix);
        return createRedisTemplate(connectionFactory, keyWrapper, valueSerializer, beanClassLoader, stringSerializer, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }

    public static RedisTemplate<String, ?> createRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @NonNull RedisKeyWrapper keyWrapper,
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
        redisKeySerializer.setKeyWrapper(keyWrapper);
        return createRedisTemplate(connectionFactory, redisKeySerializer, valueSerializer, beanClassLoader, stringSerializer, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
    }

    public static RedisTemplate<String, ?> createRedisTemplate(
            @NonNull RedisConnectionFactory connectionFactory,
            @Nullable RedisSerializer<String> keySerializer,
            @NonNull GenericSerializer<?> valueSerializer,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisSerializer<String> stringSerializer,
            @Nullable RedisSerializer<?> hashKeySerializer,
            @Nullable RedisSerializer<?> hashValueSerializer,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean enableTx,
            boolean initIt
    ) {
        return createRedisTemplate(connectionFactory, keySerializer, new DelegatableRedisSerializer<>(valueSerializer), beanClassLoader, stringSerializer, hashKeySerializer, hashValueSerializer, redisLuaScriptRepository, enableTx, initIt);
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

        if (initIt) {
            redisTemplate.afterPropertiesSet();
        }
        return redisTemplate;
    }
}
