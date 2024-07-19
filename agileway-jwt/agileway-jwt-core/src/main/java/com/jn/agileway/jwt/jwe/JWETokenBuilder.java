package com.jn.agileway.jwt.jwe;

import com.jn.agileway.jwt.JWTBuilder;

public interface JWETokenBuilder extends JWTBuilder<JWEToken, JWETokenBuilder> {

    @Override
    JWETokenBuilder withAlgorithm(String algorithm);

    @Override
    JWETokenBuilder withHeaderClaim(String claimName, Object value);

    @Override
    JWETokenBuilder withPayloadClaim(String claimName, Object value);

    @Override
    JWETokenBuilder withPayloadClaimIssuer(String issuer);

    @Override
    JWETokenBuilder withPayloadClaimSubject(String stringOrUrl);

    @Override
    JWETokenBuilder withPayloadClaimAudience(String audience);

    @Override
    JWETokenBuilder withPayloadClaimExpiration(long expirationTimeInSeconds);

    @Override
    JWETokenBuilder withPayloadClaimNotBefore(long notBeforeTimeInSeconds);

    @Override
    JWETokenBuilder withPayloadClaimIssuedAt(long issuedTimeInSeconds);

    @Override
    JWETokenBuilder withPayloadClaimJwtId(String jwtId);

    @Override
    public JWEToken build();
}
