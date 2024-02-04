package com.jn.agileway.jwt;

public interface Verifier {
    boolean verify(JWSToken token, String signature);
}
