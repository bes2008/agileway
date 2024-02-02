package com.jn.agileway.jwt.spi;

import java.util.Set;

public interface JWTService {
    AlgorithmType getAlgorithm(String algorithmName);
    Set<String> supportedJWSAlgorithms();
    Set<String> supportedJWEAlgorithms();
}
