package com.jn.agileway.jwt.jwe;


import java.util.List;

public interface JWEPlugin {
    List<String> getSupportedJWEAlgorithms();

    JWEToken parse(String jwe);

    JWETokenBuilder newJWEBuilder();

    boolean verify(JWEToken jwe);
}
