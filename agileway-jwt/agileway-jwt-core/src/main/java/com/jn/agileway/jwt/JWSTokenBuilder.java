package com.jn.agileway.jwt;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Consumer2;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Map;

public class JWSTokenBuilder implements JWTBuilder<JWSToken, JWSTokenBuilder> {
    private final boolean isCompletionEnabled;

    Map<String, Object> header = Maps.<String, Object>newHashMap();
    Map<String, Object> payload = Maps.<String, Object>newHashMap();
    public JWSTokenBuilder(){
        this(true);
    }
    public JWSTokenBuilder(boolean isCompletionEnabled){
        this.isCompletionEnabled = isCompletionEnabled;
    }

    public JWSTokenBuilder withType(String type) {
        header.put(JWTs.Headers.TYPE, type);
        return this;
    }

    @Override
    public JWSTokenBuilder withAlgorithm(String algorithm) {
        AlgorithmType algorithmType = JWTs.getAlgorithmType(algorithm);
        if(algorithmType!=AlgorithmType.NONE && algorithmType!=AlgorithmType.JWS){
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jws algorithm: {}", algorithm));
        }
        header.put(JWTs.Headers.ALGORITHM, algorithm);
        return this;
    }

    public JWSTokenBuilder withHeader(Map<String, Object> header) {
        Collects.forEach(header, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                JWSTokenBuilder.this.withHeaderClaim(key, value);
            }
        });
        return this;
    }

    @Override
    public JWSTokenBuilder withHeaderClaim(String claimName, Object value) {
        header.put(claimName, value);
        return this;
    }

    public JWSTokenBuilder withPayload(Map<String, Object> payload) {
        Collects.forEach(payload, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                JWSTokenBuilder.this.withPayloadClaim(key, value);
            }
        });
        return this;
    }

    @Override
    public JWSTokenBuilder withPayloadClaim(String claimName, Object value) {
        payload.put(claimName, value);
        return this;
    }

    public JWSTokenBuilder withPayloadClaimIssuer(String issuer){
        return withPayloadClaim(JWTs.ClaimKeys.ISSUER, issuer);
    }

    public JWSTokenBuilder withPayloadClaimSubject(String stringOrUrl){
        return withPayloadClaim(JWTs.ClaimKeys.SUBJECT, stringOrUrl);
    }

    public JWSTokenBuilder withPayloadClaimAudience(String audience){
        return withPayloadClaim(JWTs.ClaimKeys.AUDIENCE, audience);
    }

    public JWSTokenBuilder withPayloadClaimExpiration(long expirationTime){
        return withPayloadClaim(JWTs.ClaimKeys.EXPIRATION, expirationTime);
    }

    public JWSTokenBuilder withPayloadClaimNotBefore(long notBeforeTime){
        return withPayloadClaim(JWTs.ClaimKeys.NOT_BEFORE, notBeforeTime);
    }

    public JWSTokenBuilder withPayloadClaimIssuedAt(long issuedTime){
        return withPayloadClaim(JWTs.ClaimKeys.ISSUED_AT, issuedTime);
    }

    public JWSTokenBuilder withPayloadClaimJwtId(String jwtId){
        return withPayloadClaim(JWTs.ClaimKeys.JWT_ID, jwtId);
    }


    public JWSToken plain() {
        JWSToken token = build(true);
        new PlainSigner().sign(token);
        return token;
    }

    public JWSToken signWithSecretKey(String base64SecretKey){
        String algorithm = new MapAccessor(header).getString(JWTs.Headers.ALGORITHM);
        SecretKey secretKey = JWTs.toJWSSecretKey(algorithm, base64SecretKey);
        return sign(secretKey);
    }

    public JWSToken sign(SecretKey secretKey) {
        JWSToken token = build();
        new HMacSigner(secretKey).sign(token);
        return token;
    }

    public JWSToken sign(PrivateKey privateKey) {
        JWSToken token = build();
        new PKISigner(privateKey).sign(token);
        return token;
    }

    @Override
    public JWSToken build() {
        return build(false);
    }

    private JWSToken build(boolean forcePlain) {
        if (Objs.isEmpty(header)) {
            throw new JWTException("header is empty");
        }
        if (Objs.isEmpty(payload)) {
            throw new JWTException("payload is empty");
        }

        // header
        if(isCompletionEnabled) {
            if (!header.containsKey(JWTs.Headers.TYPE)) {
                header.put(JWTs.Headers.TYPE, JWTs.JWT_TYPE_DEFAULT);
            }

            if (forcePlain) {
                header.put(JWTs.Headers.ALGORITHM, JWTs.JWT_ALGORITHM_PLAIN);
            }

        }

        return new JWSToken(header, payload);
    }

}
