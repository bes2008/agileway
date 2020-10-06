package com.jn.agileway.redis.redistemplate.serialization;

import com.jn.agileway.redis.key.RedisKeyWrapper;
import com.jn.langx.util.Preconditions;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

public class RedisKeySerializer extends StringRedisSerializer {
    private RedisKeyWrapper keyWrapper = new RedisKeyWrapper();


    public RedisKeySerializer(){}

    public RedisKeySerializer(RedisKeyWrapper keyWrapper){
        setKeyWrapper(keyWrapper);
    }

    public RedisKeyWrapper getKeyWrapper() {
        return keyWrapper;
    }

    public void setKeyWrapper(RedisKeyWrapper keyWrapper) {
        this.keyWrapper = keyWrapper;
    }

    @Override
    public byte[] serialize(String key) {
        Preconditions.checkNotNull(key, "key is null");
        key = keyWrapper.wrap(key);
        return super.serialize(key);
    }

    @Override
    public String deserialize(@Nullable byte[] bytes) {
        Preconditions.checkNotEmpty(bytes, "bytes is null");
        String key = super.deserialize(bytes);
        key = keyWrapper.unwrap(key);
        return key;
    }
}
