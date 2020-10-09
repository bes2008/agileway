package com.jn.agileway.redis.core.conf;

import com.jn.agileway.redis.core.key.RedisKeyProperties;
import com.jn.easyjson.core.factory.JsonFactorys;

public class RedisTemplateProperties {
    private RedisKeyProperties key;
    private BuiltinCodecType valueCodecType;
    private boolean transactionEnabled;
    private boolean enabled;

    public RedisKeyProperties getKey() {
        return key;
    }

    public void setKey(RedisKeyProperties key) {
        this.key = key;
    }

    public BuiltinCodecType getValueCodecType() {
        return valueCodecType;
    }

    public void setValueCodecType(BuiltinCodecType valueCodecType) {
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
        return JsonFactorys.getJSONFactory().get().toJson(this);
    }
}
