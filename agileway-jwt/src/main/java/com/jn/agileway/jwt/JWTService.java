package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEPlugin;

import java.util.List;

public interface JWTService {
    AlgorithmType getAlgorithmType(String algorithmName);
    List<String> supportedJWSAlgorithms();
    List<String> supportedJWEAlgorithms();

    JWEPlugin getJWEPlugin();
    JWTParser newParser();
    JWSTokenBuilder newJWSTokenBuilder();
}
