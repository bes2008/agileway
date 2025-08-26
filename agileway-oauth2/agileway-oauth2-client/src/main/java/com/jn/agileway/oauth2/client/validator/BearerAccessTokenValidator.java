package com.jn.agileway.oauth2.client.validator;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.Payload;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.oauth2.client.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.client.exception.InvalidAccessTokenException;

import java.util.Date;

public class BearerAccessTokenValidator implements OAuth2AccessTokenValidator<JWT> {
    @Override
    public void validate(JWT accessToken) throws InvalidAccessTokenException {
        if (accessToken instanceof JWEToken) {
            // 当前对 JWE 暂不处理
            return;
        }
        Payload payload = null;
        try {
            payload = accessToken.getPayload();
        } catch (Throwable e) {
            throw new InvalidAccessTokenException("malformed access token");
        }
        if (payload == null) {
            throw new InvalidAccessTokenException("malformed access token");
        }

        Date now = new Date();
        Long iat = payload.getIssuedAt();
        if (iat != null) {
            if (now.before(new Date(iat * 1000))) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
        Long nbf = payload.getNotBefore();
        if (nbf != null) {
            if (now.before(new Date(nbf * 1000))) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
        Long expiration = payload.getExpiration();
        if (expiration != null) {
            if (now.after(new Date(expiration * 1000))) {
                throw new ExpiredAccessTokenException("access token is expired");
            }
        } else {
            Long duration = payload.getLong("expires");
            if (duration != null && iat != null) {
                if ((iat + duration) * 1000 <= now.getTime()) {
                    throw new ExpiredAccessTokenException("access token is expired");
                }
            }
        }

    }
}
