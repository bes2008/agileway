package com.jn.agileway.jwt;

public interface JWSTokenBuilder extends JWTBuilder<JWSToken,JWSTokenBuilder> {

    @Override
    public JWSTokenBuilder withAlgorithm(String algorithm) ;

    @Override
    public JWSTokenBuilder withHeaderClaim(String claimName, Object value);

    @Override
    public JWSTokenBuilder withPayloadClaim(String claimName, Object value) ;

    @Override
    public JWSToken build();

}
