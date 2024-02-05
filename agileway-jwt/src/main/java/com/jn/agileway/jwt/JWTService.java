package com.jn.agileway.jwt;

import java.util.List;

public interface JWTService {
    AlgorithmType getAlgorithmType(String algorithmName);
    List<String> supportedJWSAlgorithms();
    List<String> supportedJWEAlgorithms();

    JWEPlugin getJWEPlugin();
}
