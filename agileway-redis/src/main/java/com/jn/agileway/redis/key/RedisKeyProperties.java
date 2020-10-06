package com.jn.agileway.redis.key;

public class RedisKeyProperties {
    private String prefix = RedisKeyWrapper.PREFIX_DEFAULT;
    private String separation = RedisKeyWrapper.SEPARATOR_DEFAULT;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSeparation() {
        return separation;
    }

    public void setSeparation(String separation) {
        this.separation = separation;
    }
}
