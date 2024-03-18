package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEPlugin;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;

import java.util.Collections;
import java.util.List;

public abstract class AbstractJWTService implements JWTService {
    @Override
    public List<String> supportedJWSAlgorithms() {
        return Signs.supportedJWTSignAlgorithms();
    }

    @Override
    public final AlgorithmType getAlgorithmType(String algorithmName) {
        if (Strings.equals(JWTs.JWT_ALGORITHM_PLAIN, algorithmName)) {
            return AlgorithmType.NONE;
        }
        if (supportedJWSAlgorithms().contains(algorithmName)) {
            return AlgorithmType.JWS;
        }
        if (supportedJWEAlgorithms().contains(algorithmName)) {
            return AlgorithmType.JWE;
        }
        return AlgorithmType.UNSUPPORTED;
    }


    @Override
    public final List<String> supportedJWEAlgorithms() {
        JWEPlugin jwePlugin = getJWEPlugin();
        if (jwePlugin != null) {
            return jwePlugin.getSupportedJWEAlgorithms();
        } else {
            Loggers.getLogger(this.getClass()).warn("jwe plugin is not found");
            return Collections.emptyList();
        }
    }

    public JWSTokenBuilder newJWSTokenBuilder() {
        return new JWSTokenBuilder();
    }

    public JWTParser newParser() {
        return new DefaultJWTParser();
    }
}
