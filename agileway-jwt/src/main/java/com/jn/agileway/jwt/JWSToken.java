package com.jn.agileway.jwt;

public interface JWSToken extends JWT {
    String getSignature();
}
