package com.jn.agileway.jwt.spi;


import java.util.List;

public interface JWTService {
    AlgorithmType getAlgorithm(String algorithmName);
    List<String> supportedJWSAlgorithms();
    List<String> supportedJWEAlgorithms();
}
