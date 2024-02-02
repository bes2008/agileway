package com.jn.agileway.jwt.spi;


import java.util.List;

public interface JWTService {
    AlgorithmType getAlgorithmType(String algorithmName);
    List<String> supportedJWSAlgorithms();
    List<String> supportedJWEAlgorithms();
}
