package com.jn.agileway.jwt;

import com.jn.langx.Builder;

public interface JWTBuilder<TOKEN extends JWT,BUILDER extends JWTBuilder<TOKEN,?>> extends Builder<TOKEN> {

    public BUILDER withType(String type) ;
    BUILDER withAlgorithm(String algorithm);

    BUILDER withHeaderClaim(String claimName, Object value);

    BUILDER withPayloadClaim(String claimName, Object value);

    @Override
    TOKEN build();
}
