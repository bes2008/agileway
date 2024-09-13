package com.jn.agileway.shiro.redis.cache;

public class NoopStringKeyCodec implements StringKeyCodec<String> {
    @Override
    public String encode(String obj) {
        return obj;
    }

    @Override
    public String decode(String string) {
        return string;
    }
}
