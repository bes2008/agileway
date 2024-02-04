package com.jn.agileway.jwt;

import java.util.List;

public interface Signer {
    void sign(JWSToken token);

    List<String> supportedAlgorithms();
}
