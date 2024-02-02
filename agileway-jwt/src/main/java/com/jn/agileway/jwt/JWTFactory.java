package com.jn.agileway.jwt;

import com.jn.langx.Factory;

public interface JWTFactory<T extends JWT> extends Factory<String,T> {
    @Override
    T get(String algorithm);
}
