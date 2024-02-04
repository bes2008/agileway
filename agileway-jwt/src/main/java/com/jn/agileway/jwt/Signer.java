package com.jn.agileway.jwt;

public interface Signer {
    void sign(JWSToken token);
}
