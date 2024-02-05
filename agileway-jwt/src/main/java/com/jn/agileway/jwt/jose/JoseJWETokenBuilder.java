package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.jwt.jwe.JWETokenBuilder;

public class JoseJWETokenBuilder implements JWETokenBuilder {
    @Override
    public JWETokenBuilder withType(String type) {
        return null;
    }

    @Override
    public JWETokenBuilder withAlgorithm(String algorithm) {
        return null;
    }

    @Override
    public JWETokenBuilder withHeaderClaim(String claimName, Object value) {
        return null;
    }

    @Override
    public JWETokenBuilder withPayloadClaim(String claimName, Object value) {
        return null;
    }

    @Override
    public JWEToken build() {
        return null;
    }
}
