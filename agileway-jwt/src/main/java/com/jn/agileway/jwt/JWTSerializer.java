package com.jn.agileway.jwt;

public interface JWTSerializer<TOKEN extends JWT> {
    String serialize(TOKEN jwt);
}
