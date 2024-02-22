package com.jn.agileway.jwt;

import com.jn.langx.util.collection.Collects;

import java.util.List;

public final class PlainSigner implements Signer {

    @Override
    public void sign(JWSToken token) {
        // NOOP
    }

    private static final List<String> supportedAlgorithms = Collects.immutableList(JWTs.JWT_ALGORITHM_PLAIN);

    @Override
    public List<String> supportedAlgorithms() {
        return supportedAlgorithms;
    }
}
