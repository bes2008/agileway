package com.jn.agileway.jwt;

import com.jn.agileway.jwt.sign.Signs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;

import java.util.List;

public class DefaultJWTService implements JWTService{
    @Override
    public AlgorithmType getAlgorithmType(String algorithmName) {
        if(JWTs.JWT_ALGORITHM_PLAIN.equals(algorithmName)){
            return AlgorithmType.NONE;
        }

        return null;
    }

    @Override
    public List<String> supportedJWSAlgorithms() {
       return Signs.supportedJWTSignAlgorithms();
    }

    @Override
    public List<String> supportedJWEAlgorithms() {
        return null;
    }
}
