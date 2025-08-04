package com.jn.agileway.oauth2.authz.validator;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.Payload;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.oauth2.authz.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.authz.exception.InvalidAccessTokenException;

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

        Date expiresDate = payload.get("expires");
        if (expiresDate != null) {
            if (now.after(expiresDate)) {
                throw new ExpiredAccessTokenException("access token is expired");
            }
        }

        Date nbf = payload.getNotBeforeTime();
        if (nbf != null) {
            if (now.before(nbf)) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
        Date iat = payload.getIssueTime();
        if (iat != null) {
            if (now.before(iat)) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
    }
}
