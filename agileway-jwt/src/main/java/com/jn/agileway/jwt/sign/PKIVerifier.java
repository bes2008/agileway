package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Verifier;

public class PKIVerifier implements Verifier {
    @Override
    public boolean verify(JWSToken token, String signature) {
        return false;
    }
}
