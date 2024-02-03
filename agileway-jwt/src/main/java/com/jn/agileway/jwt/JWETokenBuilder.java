package com.jn.agileway.jwt;

public interface JWETokenBuilder extends JWTBuilder<JWEToken,JWETokenBuilder> {

    @Override
    public JWETokenBuilder withAlgorithm(String algorithm) ;

    @Override
    public JWETokenBuilder withHeaderClaim(String claimName, Object value);

    @Override
    public JWETokenBuilder withPayloadClaim(String claimName, Object value) ;

    @Override
    public JWEToken build();
}
