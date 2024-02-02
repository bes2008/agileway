package com.jn.agileway.jwt;

import com.jn.agileway.jwt.AlgorithmType;
import com.jn.agileway.jwt.JWTService;
import com.jn.langx.util.Strings;

public abstract class AbstractJWTService implements JWTService {
    @Override
    public final AlgorithmType getAlgorithmType(String algorithmName) {
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
