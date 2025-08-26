package com.jn.agileway.redis.core.conf;

import com.jn.agileway.codec.CodecType;
import com.jn.agileway.redis.core.key.RedisKeyProperties;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;

public class RedisTemplateProperties {
    private RedisKeyProperties key;
    private CodecType valueCodecType;
    private boolean transactionEnabled;
    private boolean enabled;

    public RedisKeyProperties getKey() {
        return key;
    }

    public void setKey(RedisKeyProperties key) {
        this.key = key;
    }

    public CodecType getValueCodecType() {
        return valueCodecType;
    }

    public void setValueCodecType(CodecType valueCodecType) {
        this.valueCodecType = valueCodecType;
    }

    public boolean isTransactionEnabled() {
        return transactionEnabled;
    }

    public void setTransactionEnabled(boolean transactionEnabled) {
        this.transactionEnabled = transactionEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return JsonFactorys.getJSONFactory(JsonScope.SINGLETON).get().toJson(this);
    }
}
