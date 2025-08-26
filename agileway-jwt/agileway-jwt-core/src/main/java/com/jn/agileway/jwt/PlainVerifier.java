package com.jn.agileway.jwt;

public class PlainVerifier implements Verifier {
    @Override
    public boolean verify(JWSToken token) {
        return true;
    }
}
