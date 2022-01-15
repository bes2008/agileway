package com.jn.agileway.redis.core;

import com.jn.agileway.codec.Codec;
import com.jn.agileway.codec.serialization.bson.BsonCodec;
import com.jn.agileway.codec.serialization.cbor.CborJacksonCodec;
import com.jn.agileway.codec.serialization.fse.FseCodec;
import com.jn.agileway.codec.serialization.fst.FstCodec;
import com.jn.agileway.codec.serialization.hessian.HessianCodec;
import com.jn.agileway.codec.serialization.jdk.JdkCodec;
import com.jn.agileway.codec.serialization.json.EasyjsonCodec;
import com.jn.agileway.codec.serialization.json.JacksonCodec;
import com.jn.agileway.codec.serialization.kryo.KryoCodec;
import com.jn.agileway.codec.serialization.msgpack.MsgPackCodec;
import com.jn.agileway.codec.serialization.protostuff.ProtostuffCodec;
import com.jn.agileway.codec.serialization.xson.XsonCodec;
import com.jn.agileway.redis.core.conf.BuiltinCodecType;
import com.jn.agileway.redis.core.conf.RedisTemplateProperties;
import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.core.serialization.DelegatableRedisSerializer;
import com.jn.agileway.redis.core.serialization.RedisKeySerializer;
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
        EasyjsonCodec easyjsonRedisSerializer = new EasyjsonCodec();
        easyjsonRedisSerializer.setJsonFactory(JsonFactorys.getJSONFactory(JsonScope.SINGLETON));
        easyjsonRedisSerializer.setExpectedTargetType(beanClass);
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
        EasyjsonCodec easyjsonRedisSerializer = new EasyjsonCodec();
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
            @NonNull Codec<?> valueSerializer,
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
            RedisTemplateProperties redisTemplateProperties,
            @Nullable ClassLoader beanClassLoader,
            @Nullable RedisLuaScriptRepository redisLuaScriptRepository,
            boolean initIt
    ) {

        RedisKeyWrapper keyWrapper = new RedisKeyWrapper(redisTemplateProperties.getKey());
        RedisSerializer<String> keySerializer = new RedisKeySerializer(keyWrapper);

        Codec<?> codec = newCodec(redisTemplateProperties.getValueCodecType());
        RedisSerializer<?> valueSerializer = new DelegatableRedisSerializer(codec);


        RedisSerializer hashKeySerializer = new RedisKeySerializer();

        return createRedisTemplate(connectionFactory,
                keySerializer,
                valueSerializer,
                beanClassLoader,
                stringRedisSerializer,
                hashKeySerializer,
                valueSerializer,
                redisLuaScriptRepository,
                redisTemplateProperties.isTransactionEnabled(),
                initIt
        );
    }

    public static Codec newCodec(BuiltinCodecType codecType) {
        codecType = codecType == null ? BuiltinCodecType.EASYJSON : codecType;
        Codec codec = null;
        switch (codecType) {
            case JSCKSON:
                codec = new JacksonCodec();
                break;
            case JDK:
                codec = new JdkCodec();
                break;
            case KRYO:
                codec = new KryoCodec();
                break;
            case HESSIAN:
                codec = new HessianCodec();
                break;
            case PROTOSTUFF:
                codec = new ProtostuffCodec();
                break;
            case FSE:
                codec = new FseCodec();
                break;
            case FST:
                codec = new FstCodec();
                break;
            case CBOR:
                codec = new CborJacksonCodec();
                break;
            case MSGPACK:
                codec = new MsgPackCodec();
                break;
            case XSON:
                codec = new XsonCodec();
                break;
            case BSON:
                codec = new BsonCodec();
                break;
            case EASYJSON:
            default:
                codec = new EasyjsonCodec<>(true);
                ;
                break;
        }
        return codec;

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
