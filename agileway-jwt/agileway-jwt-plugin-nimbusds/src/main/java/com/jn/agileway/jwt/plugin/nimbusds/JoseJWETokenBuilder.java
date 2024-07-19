package com.jn.agileway.jwt.plugin.nimbusds;

import com.jn.agileway.jwt.JWTs;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.jwt.jwe.JWETokenBuilder;

import java.util.Map;

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
    public JWETokenBuilder withHeader(Map<String, Object> header) {
        return null;
    }

    @Override
    public JWETokenBuilder withPayload(Map<String, Object> header) {
        return null;
    }


    public JWETokenBuilder withPayloadClaimIssuer(String issuer){
        return withPayloadClaim(JWTs.ClaimKeys.ISSUER, issuer);
    }

    public JWETokenBuilder withPayloadClaimSubject(String stringOrUrl){
        return withPayloadClaim(JWTs.ClaimKeys.SUBJECT, stringOrUrl);
    }

    public JWETokenBuilder withPayloadClaimAudience(String audience){
        return withPayloadClaim(JWTs.ClaimKeys.AUDIENCE, audience);
    }

    public JWETokenBuilder withPayloadClaimExpiration(long expirationTimeInSeconds){
        return withPayloadClaim(JWTs.ClaimKeys.EXPIRATION, expirationTimeInSeconds);
    }

    public JWETokenBuilder withPayloadClaimNotBefore(long notBeforeTimeInSeconds){
        return withPayloadClaim(JWTs.ClaimKeys.NOT_BEFORE, notBeforeTimeInSeconds);
    }

    public JWETokenBuilder withPayloadClaimIssuedAt(long issuedTimeInSeconds){
        return withPayloadClaim(JWTs.ClaimKeys.ISSUED_AT, issuedTimeInSeconds);
    }

    public JWETokenBuilder withPayloadClaimJwtId(String jwtId){
        return withPayloadClaim(JWTs.ClaimKeys.JWT_ID, jwtId);
    }

    @Override
    public JWEToken build() {
        return null;
    }
}
