package com.jn.agileway.jwt.spi;

import com.jn.langx.util.Strings;

public abstract class AbstractJWTService implements JWTService{
    @Override
    public final AlgorithmType getAlgorithm(String algorithmName) {
        if (Strings.isBlank(algorithmName) ||Strings.equalsAnyIgnoreCase("none", algorithmName)){
            return AlgorithmType.NONE;
        }
        if(supportedJWSAlgorithms().contains(algorithmName)){
            return AlgorithmType.JWS;
        }
        if(supportedJWEAlgorithms().contains(algorithmName)){
            return AlgorithmType.JWE;
        }
        return AlgorithmType.UNSUPPORTED;
    }


}
