package com.jn.agileway.oauth2.authz.validator;

import com.jn.agileway.oauth2.authz.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.authz.exception.InvalidAccessTokenException;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.util.Date;

public class BearerAccessTokenValidator implements OAuth2AccessTokenValidator<JWT> {
    @Override
    public void validate(JWT accessToken) throws InvalidAccessTokenException {
        if (accessToken instanceof EncryptedJWT) {
            // 当前对 JWE 暂不处理
            return;
        }
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = accessToken.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new InvalidAccessTokenException("malformed access token");
        }
        if (claimsSet == null) {
            throw new InvalidAccessTokenException("malformed access token");
        }

        Date now = new Date();

        Date expiresDate = claimsSet.getExpirationTime();
        if (expiresDate != null) {
            if (now.after(expiresDate)) {
                throw new ExpiredAccessTokenException("access token is expired");
            }
        }

        Date nbf = claimsSet.getNotBeforeTime();
        if (nbf != null) {
            if (now.before(nbf)) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
        Date iat = claimsSet.getIssueTime();
        if (iat != null) {
            if (now.before(iat)) {
                throw new InvalidAccessTokenException("access token is not yet valid");
            }
        }
    }
}
