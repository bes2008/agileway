package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.util.Strings;

import java.util.List;

public class JoseJWTSigner implements Signer {

    @Override
    public void sign(JWSToken token) {
        String algorithm = token.getHeader().getAlgorithm();
        algorithm=Strings.upperCase(algorithm);

    }

    @Override
    public List<String> supportedAlgorithms() {
        return null;
    }
}
