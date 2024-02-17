package com.jn.agileway.jwt;

import com.jn.langx.util.Strings;

import java.util.List;

public abstract class AbstractJWTService implements JWTService {
    @Override
    public List<String> supportedJWSAlgorithms() {
        return Signs.supportedJWTSignAlgorithms();
    }

    @Override
    public final AlgorithmType getAlgorithmType(String algorithmName) {
        if (Strings.equals(JWTs.JWT_ALGORITHM_PLAIN, algorithmName)){
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


    @Override
    public final List<String> supportedJWEAlgorithms() {
        return JWTs.getJWTService().getJWEPlugin().getSupportedJWEAlgorithms();
    }

    public JWSTokenBuilder newJWSTokenBuilder(){
        return new JWSTokenBuilder();
    }

    public JWTParser newParser(){
        return new DefaultJWTParser();
    }
}
