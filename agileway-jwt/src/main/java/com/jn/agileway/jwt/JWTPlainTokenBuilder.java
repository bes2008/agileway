package com.jn.agileway.jwt;

public interface JWTPlainTokenBuilder extends JWTBuilder<JWTPlainToken,JWTPlainTokenBuilder> {

    @Override
    public JWTPlainTokenBuilder withAlgorithm(String algorithm) ;

    @Override
    public JWTPlainTokenBuilder withHeaderClaim(String claimName, Object value);

    @Override
    public JWTPlainTokenBuilder withPayloadClaim(String claimName, Object value) ;

    @Override
    public JWTPlainToken build();
}
