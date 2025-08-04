package com.jn.agileway.oauth2.authz.userinfo;

import com.jn.agileway.oauth2.authz.exception.OAuth2Exception;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;

public class JwsOpenIdTokenParser implements OpenIdTokenParser {
    @Override
    public OpenIdToken parse(String idTokenString) {
        try {
            JWT jwt = JWTParser.parse(idTokenString);
            if (jwt instanceof EncryptedJWT) {
                // TODO 解密
                return null;
            }

            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            return new OpenIdToken(claimsSet);
        } catch (ParseException e) {
            throw new OAuth2Exception("malformed id token");
        }
    }
}
