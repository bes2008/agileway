package com.jn.agileway.jwt;

import com.jn.langx.Builder;

import java.util.Map;

public interface JWTBuilder<TOKEN extends JWT, BUILDER extends JWTBuilder<TOKEN, ?>> extends Builder<TOKEN> {

    public BUILDER withType(String type);

    BUILDER withAlgorithm(String algorithm);

    BUILDER withHeaderClaim(String claimName, Object value);

    BUILDER withHeader(Map<String, Object> header);

    BUILDER withPayloadClaim(String claimName, Object value);

    BUILDER withPayload(Map<String, Object> header);

    @Override
    TOKEN build();
}
